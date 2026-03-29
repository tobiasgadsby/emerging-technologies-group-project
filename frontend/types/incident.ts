export type IncidentStatus = "IN_PROGRESS" | "RESOLVED" | "CANCELLED";
export type PractitionerAction = "NO_ACTION_TAKEN" | "HOSPITAL_TRANSFER_URGENT";

export interface Incident {
  incidentId: number;
  patientId: number;
  patientName: string;
  practitionerId: number;
  status: IncidentStatus;
  practitionerAction?: PractitionerAction;
  reportedIssue: string;
  mlSummary: string;
  confidence: number;
  transcript: string;
  audioDuration: string;
  receivedAt: string;
}
