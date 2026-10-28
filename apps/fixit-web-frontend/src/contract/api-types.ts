// GENERATED from the LLD's OpenAPI by Aspire (contracts/codegen.ts) — do not edit.
// Import these types; never declare a type of the same name elsewhere (a check refuses it).
/* eslint-disable */

/** Assigns the request to the authenticated technician (no body fields required; actor derived from JWT) */
export interface AssignRequestBody {
}

export type ClockMode = "continuous" | "shift_hours_only";

export interface Comment {
  commentId: string;
  createdAt: string;
  requestId: string;
  authorRole: string;
  commentText: string;
  authorUserId: string;
  authorDisplayName: string;
}

export interface CommentListResponse {
  data: Array<Comment>;
  page: CursorPage;
}

export interface CreateCommentBody {
  commentText: string;
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

export interface DashboardSummary {
  openCount: number;
  resolvedCount: number;
  recentRequests: Array<MaintenanceRequest>;
  slaAtRiskCount: number;
  inProgressCount: number;
  slaBreachedCount: number;
}

export interface DowntimeSummaryResponse {
  data: Array<DowntimeSummaryRow>;
  periodEnd: string;
  periodStart: string;
  periodIncomplete: boolean;
}

export interface DowntimeSummaryRow {
  machineId: string;
  machineName: string;
  periodIncomplete?: boolean;
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

export interface ExportRequest {
  status: RequestStatus;
  priority: Priority;
  createdAt: string;
  machineId: string;
  requestId: string;
  assignedAt?: string | null;
  outOfHours: boolean;
  resolvedAt?: string | null;
  description?: string;
  machineName: string;
  slaBreached: boolean;
  slaTargetAt?: string;
  inProgressAt?: string | null;
  resolutionNote?: string | null;
  downtimeMinutes?: number | null;
  assignedToUserId?: string | null;
  submittedByUserId: string;
}

export interface ExportRequestListResponse {
  data: Array<ExportRequest>;
  page: CursorPage;
}

export interface Machine {
  name: string;
  active: boolean;
  location?: string | null;
  createdAt: string;
  machineId: string;
  deactivatedAt?: string | null;
  assetReference?: string | null;
  deactivatedByUserId?: string | null;
}

export interface MachineCreateRequest {
  name: string;
  location?: string | null;
  assetReference?: string | null;
}

export interface MachineDeactivateRequest {
  confirm: boolean;
}

export interface MachineListResponse {
  data: Array<Machine>;
  page: CursorPage;
}

export interface MachineUpdateRequest {
  name: string;
  location?: string | null;
  assetReference?: string | null;
}

export interface MaintenanceRequest {
  status: RequestStatus;
  priority: Priority;
  createdAt: string;
  machineId: string;
  requestId: string;
  slaStatus?: SlaStatus;
  assignedAt?: string | null;
  outOfHours: boolean;
  resolvedAt?: string | null;
  description: string;
  machineName: string;
  slaBreached: boolean;
  slaTargetAt: string;
  inProgressAt?: string | null;
  resolutionNote?: string | null;
  downtimeMinutes?: number | null;
  assignedToUserId?: string | null;
  submittedByUserId: string;
  assignedToDisplayName?: string | null;
}

export type Priority = "Critical" | "Medium" | "Low";

export interface RequestListResponse {
  data: Array<MaintenanceRequest>;
  page: CursorPage;
}

export type RequestStatus = "Open" | "In Progress" | "Resolved";

export interface ResolveRequestBody {
  resolutionNote: string;
}

export interface SlaConfig {
  configId: string;
  slaHours: number;
  clockMode: ClockMode;
  updatedAt: string;
  shiftEndTime?: string | null;
  priorityLevel: Priority;
  shiftStartTime?: string | null;
  updatedByUserId: string;
}

export interface SlaConfigListResponse {
  data: Array<SlaConfig>;
}

export interface SlaConfigUpdateRequest {
  slaHours: number;
  clockMode: ClockMode;
  shiftEndTime?: string | null;
  priorityLevel: Priority;
  shiftStartTime?: string | null;
}

export type SlaStatus = "ok" | "at_risk" | "breached" | "resolved_within_sla" | "resolved_breached";

/** Transitions request to In Progress (actor must be assignee) */
export interface StartRequestBody {
}
