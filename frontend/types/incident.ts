export type IncidentStatus = "IN_PROGRESS" | "RESOLVED";
export type PractitionerAction = "NO_ACTION" | "HOSPITAL_TRANSFER";

export interface Incident {
  incidentId: number;
  patientId: number;
  patientName: string;
  patientAddress?: string;
  practitionerId: number;
  incidentStatus: IncidentStatus;
  practitionerAction?: PractitionerAction;
  reportedIssue: string;
  mlSummary: string;
  confidence: number;
  transcript: string;
  audioDuration: string;
  receivedAt: string;
}
