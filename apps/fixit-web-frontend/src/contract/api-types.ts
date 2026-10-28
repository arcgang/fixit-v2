// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Import these types; never declare a type of the same name elsewhere (a check refuses it).
/* eslint-disable */

export interface AlertListResponse {
  data: Array<AlertRecord>;
  page: CursorPage;
}

export interface AlertRecord {
  id: string;
  name: string;
  state: "firing" | "resolved";
  labels?: Record<string, string>;
  source?: "cloudwatch" | "sns" | "prometheus";
  fired_at: string;
  severity: "critical" | "warning" | "info";
  created_at: string;
  updated_at?: string;
  annotations?: Record<string, string>;
  resolved_at?: string | null;
  generator_url?: string | null;
}

export interface AlertWebhookPayload {
  alerts: Array<InboundAlert>;
  source: "cloudwatch" | "sns" | "prometheus";
  correlation_id?: string;
}

export interface CommentListResponse {
  data: Array<RequestComment>;
  page: CursorPage;
}

export interface ComponentHealth {
  name: string;
  status: "healthy" | "degraded" | "unhealthy";
  message?: string;
}

export interface CreateCommentBody {
  commentText: string;
}

export interface CreateMachineBody {
  name: string;
  location?: string | null;
  assetReference?: string | null;
}

export interface CreateRequestBody {
  priority: Priority;
  machineId: string;
  description: string;
}

export interface CursorPage {
  limit: number;
  next_cursor: string | null;
}

export type DashboardDetail = DashboardSummary & { panels?: Array<DashboardPanel>; variables?: Array<Record<string, unknown>>; };

export interface DashboardListResponse {
  data: Array<DashboardSummary>;
  page: CursorPage;
}

export interface DashboardPanel {
  id: number;
  type: string;
  title: string;
  targets?: Array<Record<string, unknown>>;
  description?: string;
}

export interface DashboardResponse {
  statusCounts: { open: number; resolved: number; inProgress: number; };
  overdueRequests: Array<{ priority: Priority; createdAt: string; requestId: string; description: string; machineName: string; elapsedMinutes: number; referenceNumber: string; }>;
}

export interface DashboardSummary {
  id: string;
  uid: string;
  url: string;
  tags?: Array<string>;
  title: string;
  created_at?: string;
  updated_at?: string;
}

export interface DeactivationWarning {
  machineId: string;
  openRequests: Array<{ status: RequestStatus; requestId: string; referenceNumber: string; }>;
  openRequestCount: number;
}

export interface DowntimeReportResponse {
  rows: Array<DowntimeReportRow>;
  period: { year: number; month: number; };
  periodIncomplete: boolean;
}

export interface DowntimeReportRow {
  machineId: string;
  machineName: string;
  totalDowntimeHours: number;
  resolvedRequestCount: number;
  totalDowntimeMinutes: number;
}

export interface Error {
  code: string;
  details?: Array<Record<string, unknown>>;
  message: string;
  correlation_id: string;
}

export interface ExportDowntimeSummaryResponse {
  data: Array<DowntimeReportRow>;
  page: CursorPage;
}

export interface ExportRequestsResponse {
  data: Array<MaintenanceRequest>;
  page: CursorPage;
}

export interface HealthResponse {
  status: "healthy" | "degraded" | "unhealthy";
  checked_at?: string;
  components: Array<ComponentHealth>;
}

export interface InboundAlert {
  name: string;
  state: "firing" | "resolved";
  labels?: Record<string, string>;
  fired_at: string;
  severity?: "critical" | "warning" | "info";
  annotations?: Record<string, string>;
  resolved_at?: string | null;
  generator_url?: string;
}

export interface Machine {
  name: string;
  isActive: boolean;
  location?: string | null;
  createdAt: string;
  machineId: string;
  deactivatedAt?: string | null;
  assetReference?: string | null;
  deactivatedByUserId?: string | null;
}

export interface MachineHistoryResponse {
  data: Array<MaintenanceRequest>;
  page: CursorPage;
}

export interface MachineListResponse {
  data: Array<Machine>;
  page: CursorPage;
}

export interface MaintenanceRequest {
  status: RequestStatus;
  priority: Priority;
  createdAt: string;
  machineId: string;
  requestId: string;
  slaStatus: SlaStatus;
  assignedAt?: string | null;
  resolvedAt?: string | null;
  slaOutcome?: SlaOutcome;
  description: string;
  machineName: string;
  slaDeadlineAt: string;
  workStartedAt?: string | null;
  outOfHoursFlag: boolean;
  resolutionNote?: string | null;
  createdByUserId: string;
  referenceNumber: string;
  assignedToUserId?: string | null;
  slaThresholdMinutes: number;
  createdByDisplayName: string;
  assignedToDisplayName?: string | null;
}

export interface PatchMachineBody {
  name?: string;
  isActive?: boolean;
  location?: string | null;
  assetReference?: string | null;
  forceDeactivate?: boolean;
}

export interface PatchRequestStatusBody {
  notes?: string | null;
  status: RequestStatus;
  resolutionNote?: string | null;
  assignedToUserId?: string | null;
  assignedToDisplayName?: string | null;
}

export type Priority = "CRITICAL" | "MEDIUM" | "LOW";

export interface PutConfigBody {
  configs: Array<{ configKey: string; configValue: string; }>;
}

export interface RequestComment {
  commentId: string;
  createdAt: string;
  requestId: string;
  authorRole: string;
  commentText: string;
  authorUserId: string;
  authorDisplayName: string;
}

export interface RequestListResponse {
  data: Array<MaintenanceRequest>;
  page: CursorPage;
}

export type RequestStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED";

export interface ScrapeTarget {
  job: string;
  state: "up" | "down" | "unknown";
  labels?: Record<string, string>;
  instance: string;
  last_error?: string | null;
  last_scrape?: string;
}

export interface ScrapeTargetListResponse {
  data: Array<ScrapeTarget>;
  page: CursorPage;
}

export type SlaOutcome = "MET" | "BREACHED" | "PENDING";

export type SlaStatus = "ON_TRACK" | "AT_RISK" | "BREACHED" | "MET";

export interface SloListResponse {
  data: Array<SloStatus>;
  page: CursorPage;
}

export interface SloStatus {
  id: string;
  name: string;
  status?: "ok" | "warning" | "critical";
  target: number;
  evaluated_at?: string;
  window_hours: number;
  current_burn_rate: number;
  error_budget_remaining_pct: number;
}

export interface SseEvent {
  payload: Record<string, unknown>;
  eventType: "REQUEST_CREATED" | "REQUEST_STATUS_CHANGED" | "REQUEST_ASSIGNED" | "COMMENT_ADDED" | "MACHINE_UPDATED";
  occurredAt: string;
}

export interface StatusHistoryEntry {
  notes?: string | null;
  toStatus: string;
  historyId: string;
  requestId: string;
  fromStatus?: string | null;
  transitionedAt: string;
  transitionedByRole: string;
  transitionedByUserId: string;
}

export interface SystemConfig {
  configKey: string;
  updatedAt: string;
  configValue: string;
  description?: string | null;
  updatedByUserId: string;
}

export interface SystemConfigListResponse {
  data: Array<SystemConfig>;
}
