-- prometheus-grafana owns its own operational metadata tables
-- These are separate from the Aurora PostgreSQL instance used by fixit-spring-boot-api;
-- they reside in a dedicated 'observability' schema within the same Aurora cluster.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

CREATE SCHEMA IF NOT EXISTS observability;

-- -----------------------------------------------------------------------
-- alert_record
-- Persists every inbound alert notification received via the webhook
-- endpoint (from CloudWatch Alarms forwarded through SNS, or from
-- Prometheus Alertmanager). Immutable once written; state transitions
-- are appended as new rows via alert_state_transition.
-- -----------------------------------------------------------------------
CREATE TABLE observability.alert_record (
    id                UUID        NOT NULL DEFAULT uuid_generate_v4(),
    name              TEXT        NOT NULL,
    state             TEXT        NOT NULL CHECK (state IN ('firing','resolved')),
    severity          TEXT        NOT NULL CHECK (severity IN ('critical','warning','info')),
    source            TEXT        NOT NULL CHECK (source IN ('cloudwatch','sns','prometheus')),
    fired_at          TIMESTAMPTZ NOT NULL,
    resolved_at       TIMESTAMPTZ,
    labels            JSONB       NOT NULL DEFAULT '{}',
    annotations       JSONB       NOT NULL DEFAULT '{}',
    generator_url     TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_alert_record PRIMARY KEY (id)
);

CREATE INDEX idx_alert_record_state       ON observability.alert_record (state);
CREATE INDEX idx_alert_record_severity    ON observability.alert_record (severity);
CREATE INDEX idx_alert_record_fired_at    ON observability.alert_record (fired_at DESC);
CREATE INDEX idx_alert_record_name_trgm   ON observability.alert_record USING gin (name gin_trgm_ops);
CREATE INDEX idx_alert_record_labels      ON observability.alert_record USING gin (labels);

COMMENT ON TABLE observability.alert_record IS
    'Immutable log of every alert notification received. Retention: 90 days operational; purge via scheduled job.';

-- -----------------------------------------------------------------------
-- alert_state_transition
-- Append-only audit trail of every state change for an alert_record.
-- -----------------------------------------------------------------------
CREATE TABLE observability.alert_state_transition (
    id                UUID        NOT NULL DEFAULT uuid_generate_v4(),
    alert_record_id   UUID        NOT NULL,
    from_state        TEXT,
    to_state          TEXT        NOT NULL CHECK (to_state IN ('firing','resolved')),
    transitioned_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_alert_state_transition PRIMARY KEY (id),
    CONSTRAINT fk_ast_alert_record FOREIGN KEY (alert_record_id)
        REFERENCES observability.alert_record (id) ON DELETE CASCADE
);

CREATE INDEX idx_ast_alert_record_id ON observability.alert_state_transition (alert_record_id);
CREATE INDEX idx_ast_transitioned_at ON observability.alert_state_transition (transitioned_at DESC);

-- -----------------------------------------------------------------------
-- slo_definition
-- Stores the definition of each SLO tracked by the observability stack.
-- -----------------------------------------------------------------------
CREATE TABLE observability.slo_definition (
    id                TEXT        NOT NULL,
    name              TEXT        NOT NULL,
    description       TEXT,
    target_ratio      NUMERIC(6,5) NOT NULL CHECK (target_ratio > 0 AND target_ratio <= 1),
    window_hours      INTEGER     NOT NULL DEFAULT 720,
    metric_query      TEXT        NOT NULL,
    is_active         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_slo_definition PRIMARY KEY (id)
);

CREATE INDEX idx_slo_definition_is_active ON observability.slo_definition (is_active);

COMMENT ON TABLE observability.slo_definition IS
    'SLO definitions: availability (99.5%), p95 latency (<=3s), SSE connection count, SLA breach rate, EKS resource utilisation.';

-- -----------------------------------------------------------------------
-- slo_evaluation
-- Periodic snapshots of SLO burn-rate and error-budget, written by the
-- Prometheus recording-rule evaluation job (via a sidecar writer).
-- -----------------------------------------------------------------------
CREATE TABLE observability.slo_evaluation (
    id                        UUID        NOT NULL DEFAULT uuid_generate_v4(),
    slo_id                    TEXT        NOT NULL,
    evaluated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    current_burn_rate         NUMERIC(10,6) NOT NULL,
    error_budget_remaining_pct NUMERIC(6,3) NOT NULL,
    status                    TEXT        NOT NULL CHECK (status IN ('ok','warning','critical')),
    created_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_slo_evaluation PRIMARY KEY (id),
    CONSTRAINT fk_slo_eval_definition FOREIGN KEY (slo_id)
        REFERENCES observability.slo_definition (id) ON DELETE CASCADE
);

CREATE INDEX idx_slo_evaluation_slo_id       ON observability.slo_evaluation (slo_id);
CREATE INDEX idx_slo_evaluation_evaluated_at ON observability.slo_evaluation (evaluated_at DESC);
CREATE INDEX idx_slo_evaluation_status       ON observability.slo_evaluation (status);

COMMENT ON TABLE observability.slo_evaluation IS
    'Time-series snapshots of SLO health. Retention: 90 days; older rows purged by scheduled job.';

-- -----------------------------------------------------------------------
-- scrape_target_state
-- Latest known UP/DOWN state for each Prometheus scrape target,
-- upserted on every scrape cycle by the Prometheus federation sidecar.
-- -----------------------------------------------------------------------
CREATE TABLE observability.scrape_target_state (
    id            UUID        NOT NULL DEFAULT uuid_generate_v4(),
    job           TEXT        NOT NULL,
    instance      TEXT        NOT NULL,
    state         TEXT        NOT NULL CHECK (state IN ('up','down','unknown')),
    last_scrape   TIMESTAMPTZ,
    last_error    TEXT,
    labels        JSONB       NOT NULL DEFAULT '{}',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_scrape_target_state PRIMARY KEY (id),
    CONSTRAINT uq_scrape_target_job_instance UNIQUE (job, instance)
);

CREATE INDEX idx_scrape_target_state_state ON observability.scrape_target_state (state);
CREATE INDEX idx_scrape_target_state_job   ON observability.scrape_target_state (job);

COMMENT ON TABLE observability.scrape_target_state IS
    'Latest scrape health for fixit-spring-boot-api Micrometer endpoint and Keycloak metrics endpoint.';

-- -----------------------------------------------------------------------
-- dashboard_metadata
-- Mirrors Grafana dashboard UIDs and titles for the API metadata endpoint.
-- Kept in sync by a Grafana provisioning sidecar on startup.
-- -----------------------------------------------------------------------
CREATE TABLE observability.dashboard_metadata (
    id          TEXT        NOT NULL,
    uid         TEXT        NOT NULL,
    title       TEXT        NOT NULL,
    url         TEXT        NOT NULL,
    tags        JSONB       NOT NULL DEFAULT '[]',
    panel_json  JSONB       NOT NULL DEFAULT '[]',
    variables   JSONB       NOT NULL DEFAULT '[]',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_dashboard_metadata PRIMARY KEY (id),
    CONSTRAINT uq_dashboard_metadata_uid UNIQUE (uid)
);

CREATE INDEX idx_dashboard_metadata_title_trgm ON observability.dashboard_metadata USING gin (title gin_trgm_ops);
CREATE INDEX idx_dashboard_metadata_tags       ON observability.dashboard_metadata USING gin (tags);
