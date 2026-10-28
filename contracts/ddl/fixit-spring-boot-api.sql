CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- ENUM TYPES
-- ============================================================
CREATE TYPE request_status AS ENUM ('OPEN', 'IN_PROGRESS', 'RESOLVED');
CREATE TYPE request_priority AS ENUM ('CRITICAL', 'MEDIUM', 'LOW');
CREATE TYPE sla_outcome AS ENUM ('MET', 'BREACHED', 'PENDING');
CREATE TYPE sla_clock_mode AS ENUM ('CONTINUOUS', 'SHIFT_HOURS_ONLY');

-- ============================================================
-- MACHINE
-- ============================================================
CREATE TABLE machine (
    machine_id              UUID            NOT NULL DEFAULT uuid_generate_v4(),
    name                    TEXT            NOT NULL,
    asset_reference         TEXT,
    location                TEXT,
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deactivated_at          TIMESTAMPTZ,
    deactivated_by_user_id  UUID,
    CONSTRAINT pk_machine PRIMARY KEY (machine_id)
);

CREATE INDEX idx_machine_is_active ON machine (is_active);
CREATE INDEX idx_machine_name ON machine (name);

COMMENT ON TABLE machine IS 'Master machine register. Soft-delete via is_active flag. Classification: public.';

-- ============================================================
-- MAINTENANCE REQUEST
-- ============================================================
CREATE TABLE maintenance_request (
    request_id              UUID            NOT NULL DEFAULT uuid_generate_v4(),
    machine_id              UUID            NOT NULL,
    description             TEXT            NOT NULL,
    priority                request_priority NOT NULL,
    status                  request_status  NOT NULL DEFAULT 'OPEN',
    created_by_user_id      UUID            NOT NULL,
    created_by_display_name TEXT            NOT NULL,
    assigned_to_user_id     UUID,
    assigned_to_display_name TEXT,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    assigned_at             TIMESTAMPTZ,
    work_started_at         TIMESTAMPTZ,
    resolved_at             TIMESTAMPTZ,
    resolution_note         TEXT,
    sla_deadline_at         TIMESTAMPTZ     NOT NULL,
    sla_outcome             sla_outcome,
    out_of_hours_flag       BOOLEAN         NOT NULL DEFAULT FALSE,
    reference_number        TEXT            NOT NULL,
    CONSTRAINT pk_maintenance_request PRIMARY KEY (request_id),
    CONSTRAINT uq_maintenance_request_reference UNIQUE (reference_number),
    CONSTRAINT fk_maintenance_request_machine FOREIGN KEY (machine_id) REFERENCES machine (machine_id)
);

CREATE INDEX idx_mr_machine_id ON maintenance_request (machine_id);
CREATE INDEX idx_mr_status ON maintenance_request (status);
CREATE INDEX idx_mr_priority ON maintenance_request (priority);
CREATE INDEX idx_mr_created_by_user_id ON maintenance_request (created_by_user_id);
CREATE INDEX idx_mr_assigned_to_user_id ON maintenance_request (assigned_to_user_id);
CREATE INDEX idx_mr_created_at ON maintenance_request (created_at);
CREATE INDEX idx_mr_status_priority_created ON maintenance_request (status, priority, created_at);
CREATE INDEX idx_mr_resolved_at ON maintenance_request (resolved_at);
CREATE INDEX idx_mr_sla_deadline_at ON maintenance_request (sla_deadline_at);

COMMENT ON TABLE maintenance_request IS 'Core request entity. PII fields: created_by_user_id, created_by_display_name, assigned_to_user_id, assigned_to_display_name. Classification: confidential. Retention: 7 years from resolved_at.';

-- ============================================================
-- REQUEST STATUS HISTORY (immutable audit log)
-- ============================================================
CREATE TABLE request_status_history (
    history_id              UUID            NOT NULL DEFAULT uuid_generate_v4(),
    request_id              UUID            NOT NULL,
    from_status             TEXT,
    to_status               TEXT            NOT NULL,
    transitioned_by_user_id UUID            NOT NULL,
    transitioned_by_role    TEXT            NOT NULL,
    transitioned_at         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    notes                   TEXT,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_request_status_history PRIMARY KEY (history_id),
    CONSTRAINT fk_rsh_request FOREIGN KEY (request_id) REFERENCES maintenance_request (request_id)
);

CREATE INDEX idx_rsh_request_id ON request_status_history (request_id);
CREATE INDEX idx_rsh_transitioned_at ON request_status_history (transitioned_at);
CREATE INDEX idx_rsh_transitioned_by_user_id ON request_status_history (transitioned_by_user_id);

COMMENT ON TABLE request_status_history IS 'Immutable audit log of all status transitions. No UPDATE or DELETE permitted. PII: transitioned_by_user_id. Classification: confidential.';

-- ============================================================
-- REQUEST COMMENT (append-only)
-- ============================================================
CREATE TABLE request_comment (
    comment_id              UUID            NOT NULL DEFAULT uuid_generate_v4(),
    request_id              UUID            NOT NULL,
    author_user_id          UUID            NOT NULL,
    author_display_name     TEXT            NOT NULL,
    author_role             TEXT            NOT NULL,
    comment_text            TEXT            NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_request_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_rc_request FOREIGN KEY (request_id) REFERENCES maintenance_request (request_id)
);

CREATE INDEX idx_rc_request_id ON request_comment (request_id);
CREATE INDEX idx_rc_created_at ON request_comment (request_id, created_at);
CREATE INDEX idx_rc_author_user_id ON request_comment (author_user_id);

COMMENT ON TABLE request_comment IS 'Append-only comment thread per request. No UPDATE or DELETE permitted. PII: author_user_id, author_display_name. Classification: confidential.';

-- ============================================================
-- SLA RECORD
-- ============================================================
CREATE TABLE sla_record (
    sla_record_id           UUID            NOT NULL DEFAULT uuid_generate_v4(),
    request_id              UUID            NOT NULL,
    priority                request_priority NOT NULL,
    sla_threshold_minutes   INTEGER         NOT NULL,
    sla_deadline_at         TIMESTAMPTZ     NOT NULL,
    sla_clock_mode          sla_clock_mode  NOT NULL DEFAULT 'CONTINUOUS',
    sla_outcome             sla_outcome,
    breach_duration_minutes INTEGER,
    evaluated_at            TIMESTAMPTZ,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_sla_record PRIMARY KEY (sla_record_id),
    CONSTRAINT uq_sla_record_request UNIQUE (request_id),
    CONSTRAINT fk_sla_request FOREIGN KEY (request_id) REFERENCES maintenance_request (request_id)
);

CREATE INDEX idx_sla_request_id ON sla_record (request_id);
CREATE INDEX idx_sla_deadline_at ON sla_record (sla_deadline_at);
CREATE INDEX idx_sla_outcome ON sla_record (sla_outcome);

COMMENT ON TABLE sla_record IS 'SLA calculation record per request. Classification: confidential.';

-- ============================================================
-- SYSTEM CONFIG
-- ============================================================
CREATE TABLE system_config (
    config_key              TEXT            NOT NULL,
    config_value            TEXT            NOT NULL,
    description             TEXT,
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_by_user_id      UUID            NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_system_config PRIMARY KEY (config_key)
);

COMMENT ON TABLE system_config IS 'Runtime configuration: SLA thresholds, shift hours, SLA clock mode. PII: updated_by_user_id. Classification: public.';

-- Seed default config keys
INSERT INTO system_config (config_key, config_value, description, updated_by_user_id)
VALUES
  ('sla.critical.threshold_minutes', '60',  'SLA threshold for CRITICAL priority requests in minutes', uuid_generate_v4()),
  ('sla.medium.threshold_minutes',   '240', 'SLA threshold for MEDIUM priority requests in minutes',   uuid_generate_v4()),
  ('sla.low.threshold_minutes',      '480', 'SLA threshold for LOW priority requests in minutes',      uuid_generate_v4()),
  ('sla.clock_mode',                 'CONTINUOUS', 'SLA clock mode: CONTINUOUS or SHIFT_HOURS_ONLY',  uuid_generate_v4()),
  ('shift.start_time',               '06:00', 'Shift start time in HH:MM (local)',                    uuid_generate_v4()),
  ('shift.end_time',                 '22:00', 'Shift end time in HH:MM (local)',                      uuid_generate_v4()),
  ('shift.timezone',                 'Europe/London', 'Timezone for shift hours calculation',         uuid_generate_v4()),
  ('overdue.threshold_minutes',      '1440', 'Minutes after which an OPEN request is flagged overdue (default 24h)', uuid_generate_v4()),
  ('sla.at_risk.lead_minutes',       '15',   'Minutes before SLA breach to flag as AT_RISK',          uuid_generate_v4())
ON CONFLICT (config_key) DO NOTHING;

-- ============================================================
-- CONFIG CHANGE LOG (immutable audit)
-- ============================================================
CREATE TABLE config_change_log (
    change_id               UUID            NOT NULL DEFAULT uuid_generate_v4(),
    config_key              TEXT            NOT NULL,
    old_value               TEXT,
    new_value               TEXT            NOT NULL,
    changed_by_user_id      UUID            NOT NULL,
    changed_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_config_change_log PRIMARY KEY (change_id)
);

CREATE INDEX idx_ccl_config_key ON config_change_log (config_key);
CREATE INDEX idx_ccl_changed_at ON config_change_log (changed_at);
CREATE INDEX idx_ccl_changed_by ON config_change_log (changed_by_user_id);

COMMENT ON TABLE config_change_log IS 'Immutable audit log of all system_config changes. No UPDATE or DELETE permitted. PII: changed_by_user_id. Classification: confidential.';
