-- Seed data for the local stack: 3 synthetic rows per table, derived from the LLD's
-- Postgres DDL. Deterministic; contains no real personal data. Load after the schema.
BEGIN;

INSERT INTO users (user_id, display_name, role, last_login_at, anonymised_at)
VALUES
  ('7750ca6c-8ee3-5b16-953b-eb07353bc531', 'Sarah Chen', 'Operator', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z'),
  ('f33acafd-07ae-5f05-9842-d1ae196be265', 'James Okafor', 'Technician', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z'),
  ('fea4fa4b-ea03-50a0-a404-abfd02cf1652', 'Priya Nair', 'Supervisor', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z')
ON CONFLICT DO NOTHING;

INSERT INTO machines (machine_id, name, asset_reference, location, deactivated_at, deactivated_by_user_id)
VALUES
  ('44907b21-c8ef-512b-af7b-1d974276befe', 'Sarah Chen', 'MAC-001', 'location 1', '2026-01-01T09:00:00Z', '7750ca6c-8ee3-5b16-953b-eb07353bc531'),
  ('212d57eb-6501-53e0-9d8c-016dea6d9070', 'James Okafor', 'MAC-002', 'location 2', '2026-01-02T09:00:00Z', 'f33acafd-07ae-5f05-9842-d1ae196be265'),
  ('817d0b8c-2633-5f64-a314-6bce577e08d6', 'Priya Nair', 'MAC-003', 'location 3', '2026-01-03T09:00:00Z', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652')
ON CONFLICT DO NOTHING;

INSERT INTO sla_config (config_id, priority_level, sla_hours, clock_mode, shift_start_time, shift_end_time, updated_by_user_id)
VALUES
  ('b7c90a89-0564-5bcb-a304-6013e26eea6f', 'Critical', 1250.5, 'continuous', 'shift start time 1', 'shift end time 1', '7750ca6c-8ee3-5b16-953b-eb07353bc531'),
  ('3ab55ac2-a59f-50ef-9715-08b23e1ea501', 'Medium', 2501.0, 'shift_hours_only', 'shift start time 2', 'shift end time 2', 'f33acafd-07ae-5f05-9842-d1ae196be265'),
  ('58c3fe76-1e31-514b-8191-b48db1db3571', 'Low', 3751.5, 'continuous', 'shift start time 3', 'shift end time 3', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652')
ON CONFLICT DO NOTHING;

INSERT INTO maintenance_requests (request_id, machine_id, submitted_by_user_id, assigned_to_user_id, description, priority, sla_target_at, resolution_note, assigned_at, in_progress_at, resolved_at, downtime_minutes)
VALUES
  ('d9ba95b3-f9a9-51eb-aa8b-5ef9ef2706c6', '44907b21-c8ef-512b-af7b-1d974276befe', '7750ca6c-8ee3-5b16-953b-eb07353bc531', '7750ca6c-8ee3-5b16-953b-eb07353bc531', 'description 1', 'Critical', '2026-01-01T09:00:00Z', 'resolution note 1', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z', '2026-01-01T09:00:00Z', 10),
  ('1627261c-509a-5c74-8332-7db426990af5', '212d57eb-6501-53e0-9d8c-016dea6d9070', 'f33acafd-07ae-5f05-9842-d1ae196be265', 'f33acafd-07ae-5f05-9842-d1ae196be265', 'description 2', 'Medium', '2026-01-02T09:00:00Z', 'resolution note 2', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z', '2026-01-02T09:00:00Z', 20),
  ('d7d43d3a-1289-50dc-a61a-8fea92b661a7', '817d0b8c-2633-5f64-a314-6bce577e08d6', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652', 'description 3', 'Low', '2026-01-03T09:00:00Z', 'resolution note 3', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z', '2026-01-03T09:00:00Z', 30)
ON CONFLICT DO NOTHING;

INSERT INTO request_comments (comment_id, request_id, author_user_id, author_role, comment_text)
VALUES
  ('36b3fe8f-c166-52f9-b2ab-965309139e30', 'd9ba95b3-f9a9-51eb-aa8b-5ef9ef2706c6', '7750ca6c-8ee3-5b16-953b-eb07353bc531', 'Operator', 'comment text 1'),
  ('5e0085ea-d239-5056-8fdc-8d6822e5da25', '1627261c-509a-5c74-8332-7db426990af5', 'f33acafd-07ae-5f05-9842-d1ae196be265', 'Technician', 'comment text 2'),
  ('2a04a872-4bc4-5a60-9a04-0f04c55cd522', 'd7d43d3a-1289-50dc-a61a-8fea92b661a7', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652', 'Supervisor', 'comment text 3')
ON CONFLICT DO NOTHING;

INSERT INTO audit_events (event_id, request_id, actor_user_id, action, outcome, metadata)
VALUES
  ('6e2fdc96-3f2c-59c2-acbe-cb6f80686341', 'd9ba95b3-f9a9-51eb-aa8b-5ef9ef2706c6', '7750ca6c-8ee3-5b16-953b-eb07353bc531', 'action 1', 'success', '{}'),
  ('abd7a61a-7bba-55c8-b54e-6b37a760d114', '1627261c-509a-5c74-8332-7db426990af5', 'f33acafd-07ae-5f05-9842-d1ae196be265', 'action 2', 'failure', '{}'),
  ('4fb43b52-4451-5548-8d32-50d55365e3aa', 'd7d43d3a-1289-50dc-a61a-8fea92b661a7', 'fea4fa4b-ea03-50a0-a404-abfd02cf1652', 'action 3', 'success', '{}')
ON CONFLICT DO NOTHING;

COMMIT;
