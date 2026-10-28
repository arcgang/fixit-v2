-- Seed data for the local stack: 3 synthetic rows per table, derived from the LLD's
-- Postgres DDL. Deterministic; contains no real personal data. Load after the schema.
BEGIN;

INSERT INTO machine (machine_id, name, asset_reference, location, deactivated_at, deactivated_by_user_id)
VALUES
  ('dffe7aeb-50bb-5e5c-bc05-dea24ec568cf', 'Sarah Chen', 'MAC-001', 'location 1', '2026-01-01T09:00:00Z', 'd0a691e2-f51f-5c01-b98d-4f4a0196a21d'),
  ('e5961af0-9b42-5108-a4f4-b41e468bd385', 'James Okafor', 'MAC-002', 'location 2', '2026-01-02T09:00:00Z', 'de21a6b7-4419-5bcd-a189-371485a9ca80'),
  ('f397508b-15c4-5e21-956b-ef4e6fddaa3d', 'Priya Nair', 'MAC-003', 'location 3', '2026-01-03T09:00:00Z', 'af4a3fd7-1575-523c-8062-d028416fbcbf')
ON CONFLICT DO NOTHING;

INSERT INTO system_config (config_key, config_value, description, updated_by_user_id)
VALUES
  ('config key 1', 'config value 1', 'description 1', '41b5b149-70b4-5ffd-bf53-669a3270a1f8'),
  ('config key 2', 'config value 2', 'description 2', 'cb71cfaf-0e3d-5857-b3ec-f8bdd321bd2e'),
  ('config key 3', 'config value 3', 'description 3', 'ab029028-429f-512a-918f-021b0260e4ad')
ON CONFLICT DO NOTHING;

INSERT INTO config_change_log (change_id, config_key, old_value, new_value, changed_by_user_id)
VALUES
  ('3ceb486a-5a24-5a05-a2fe-19851ea2111a', 'config key 1', 'old value 1', 'new value 1', '1ac4bcff-b306-53ab-b2cd-e5d5027b0a94'),
  ('5357b704-3321-5938-bfd1-0779ada63550', 'config key 2', 'old value 2', 'new value 2', '1959753c-b751-545d-92fe-8788c582d16b'),
  ('97e74720-3395-55f7-b5a8-e976583a1aaa', 'config key 3', 'old value 3', 'new value 3', 'b8425627-6682-5034-b03d-d7bc72b911da')
ON CONFLICT DO NOTHING;

INSERT INTO observability.alert_record (id, name, state, severity, source, fired_at, resolved_at, generator_url)
VALUES
  ('1354601b-ec14-5d46-8a65-72cb78039de3', 'Sarah Chen', 'firing', 'critical', 'cloudwatch', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z', 'https://example.com/alert_record/1'),
  ('1ec7f8d7-4b64-599f-829f-b0090387f108', 'James Okafor', 'resolved', 'warning', 'sns', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z', 'https://example.com/alert_record/2'),
  ('dbf97a45-3aa3-5724-a302-80530925500e', 'Priya Nair', 'firing', 'info', 'prometheus', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z', 'https://example.com/alert_record/3')
ON CONFLICT DO NOTHING;

INSERT INTO observability.slo_definition (id, name, description, target_ratio, metric_query)
VALUES
  ('SLO-001', 'Sarah Chen', 'description 1', 1.0, 'metric query 1'),
  ('SLO-002', 'James Okafor', 'description 2', 1.0, 'metric query 2'),
  ('SLO-003', 'Priya Nair', 'description 3', 1.0, 'metric query 3')
ON CONFLICT DO NOTHING;

INSERT INTO observability.scrape_target_state (id, job, instance, state, last_scrape, last_error)
VALUES
  ('3e21826a-0459-526b-9fd8-d371dbb56293', 'job 1', 'instance 1', 'up', '2026-01-01T09:00:00Z', 'last error 1'),
  ('c1a67244-6fd2-52fb-bf9a-01197b2e03cd', 'job 2', 'instance 2', 'down', '2026-01-02T09:00:00Z', 'last error 2'),
  ('d7bb57fa-5cf3-565f-9bee-0c5e266ba10c', 'job 3', 'instance 3', 'unknown', '2026-01-03T09:00:00Z', 'last error 3')
ON CONFLICT DO NOTHING;

INSERT INTO observability.dashboard_metadata (id, uid, title, url)
VALUES
  ('DAS-001', 'uid 1', 'title 1', 'https://example.com/dashboard_metadata/1'),
  ('DAS-002', 'uid 2', 'title 2', 'https://example.com/dashboard_metadata/2'),
  ('DAS-003', 'uid 3', 'title 3', 'https://example.com/dashboard_metadata/3')
ON CONFLICT DO NOTHING;

INSERT INTO maintenance_request (request_id, machine_id, description, priority, created_by_user_id, created_by_display_name, assigned_to_user_id, assigned_to_display_name, assigned_at, work_started_at, resolved_at, resolution_note, sla_deadline_at, sla_outcome, reference_number)
VALUES
  ('551606df-d3bc-5313-8649-811c01e9ccae', 'dffe7aeb-50bb-5e5c-bc05-dea24ec568cf', 'description 1', 'CRITICAL', 'a9a261f8-ccd9-51b3-93f1-3333ec68f259', 'created by display name 1', '2f985023-d7c1-56d2-ae62-5d8c88a25455', 'assigned to display name 1', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z', 'resolution note 1', '2026-01-01T09:00:00Z', 'MET', 'MAI-001'),
  ('7f63cb43-3193-56ce-b519-787a241b82d6', 'e5961af0-9b42-5108-a4f4-b41e468bd385', 'description 2', 'MEDIUM', '251fc281-399e-5749-9517-8a1943d87248', 'created by display name 2', '60099905-bd4f-5d9c-86eb-12655e80a38a', 'assigned to display name 2', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z', 'resolution note 2', '2026-01-02T09:00:00Z', 'BREACHED', 'MAI-002'),
  ('0b0a2303-b5cb-55f6-ae65-6d995c4c5954', 'f397508b-15c4-5e21-956b-ef4e6fddaa3d', 'description 3', 'LOW', '3c98980b-0101-5d40-83bc-51acb44cb774', 'created by display name 3', '1c965ea9-d9e3-50a0-a7f8-c5d2e12b1bed', 'assigned to display name 3', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z', 'resolution note 3', '2026-01-03T09:00:00Z', 'PENDING', 'MAI-003')
ON CONFLICT DO NOTHING;

INSERT INTO observability.alert_state_transition (id, alert_record_id, from_state, to_state)
VALUES
  ('c1946836-4ee6-5519-8a8c-6d833277f77c', '1354601b-ec14-5d46-8a65-72cb78039de3', 'from state 1', 'firing'),
  ('67bd89e7-8afc-5f9b-aa46-cd5cd197565f', '1ec7f8d7-4b64-599f-829f-b0090387f108', 'from state 2', 'resolved'),
  ('e2f68fde-6273-5556-8ffb-f3fab972ea8d', 'dbf97a45-3aa3-5724-a302-80530925500e', 'from state 3', 'firing')
ON CONFLICT DO NOTHING;

INSERT INTO observability.slo_evaluation (id, slo_id, current_burn_rate, error_budget_remaining_pct, status)
VALUES
  ('e4ccd42e-94bb-59bc-b93a-ecc25505645d', 'SLO-001', 1250.5, 1250.5, 'ok'),
  ('78902cd7-da56-56c4-a26b-d520cea5a026', 'SLO-002', 2501.0, 2501.0, 'warning'),
  ('d42757bd-bca4-508f-a72b-53af326b49fc', 'SLO-003', 3751.5, 3751.5, 'critical')
ON CONFLICT DO NOTHING;

INSERT INTO request_status_history (history_id, request_id, from_status, to_status, transitioned_by_user_id, transitioned_by_role, notes)
VALUES
  ('4af8132f-63a3-535e-9cee-d6188d0c0017', '551606df-d3bc-5313-8649-811c01e9ccae', 'from status 1', 'to status 1', 'ddaecc1a-17d4-530c-b95b-27bbf735c064', 'transitioned by role 1', 'notes 1'),
  ('65f5eeee-3b85-5a6d-8e5c-985f5861434b', '7f63cb43-3193-56ce-b519-787a241b82d6', 'from status 2', 'to status 2', 'b2990c41-bba0-5cf8-bb1c-9447936cfdeb', 'transitioned by role 2', 'notes 2'),
  ('73a57218-ee9d-523b-ab60-5d18886aa8d8', '0b0a2303-b5cb-55f6-ae65-6d995c4c5954', 'from status 3', 'to status 3', 'a6c93c58-abe4-5b2d-be84-390c39b742da', 'transitioned by role 3', 'notes 3')
ON CONFLICT DO NOTHING;

INSERT INTO request_comment (comment_id, request_id, author_user_id, author_display_name, author_role, comment_text)
VALUES
  ('585343c6-2176-5b72-b419-1bc826e3c0f3', '551606df-d3bc-5313-8649-811c01e9ccae', '18063aa9-a9cd-5ea3-9eac-309fa5af1472', 'author display name 1', 'author role 1', 'comment text 1'),
  ('05a2f3a2-0dd1-55db-a6ef-e7644b30a6d6', '7f63cb43-3193-56ce-b519-787a241b82d6', 'dc184bf9-0f9d-5000-a0b4-a78cbd69f3e8', 'author display name 2', 'author role 2', 'comment text 2'),
  ('d301e4b4-bf50-5431-92a0-a1e6989390f9', '0b0a2303-b5cb-55f6-ae65-6d995c4c5954', '7e1010d3-088a-51cf-84d6-4ddcbff41dd6', 'author display name 3', 'author role 3', 'comment text 3')
ON CONFLICT DO NOTHING;

INSERT INTO sla_record (sla_record_id, request_id, priority, sla_threshold_minutes, sla_deadline_at, sla_outcome, breach_duration_minutes, evaluated_at)
VALUES
  ('9bfcd82a-329f-5296-8f1c-d495d3ab6bed', '551606df-d3bc-5313-8649-811c01e9ccae', 'CRITICAL', 10, '2026-01-01T09:00:00Z', 'MET', 10, '2026-01-01T09:00:00Z'),
  ('bf430529-3173-5bb0-93e7-ea1ce4f2223a', '7f63cb43-3193-56ce-b519-787a241b82d6', 'MEDIUM', 20, '2026-01-02T09:00:00Z', 'BREACHED', 20, '2026-01-02T09:00:00Z'),
  ('dbf004e9-29bb-5424-824a-45b6982c83e7', '0b0a2303-b5cb-55f6-ae65-6d995c4c5954', 'LOW', 30, '2026-01-03T09:00:00Z', 'PENDING', 30, '2026-01-03T09:00:00Z')
ON CONFLICT DO NOTHING;

COMMIT;
