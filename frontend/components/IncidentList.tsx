"use client";

import { Incident } from "@/types/incident";

interface IncidentListProps {
  incidents: Incident[];
  selectedId: number | null;
  onSelectIncident: (id: number) => void;
}

export function IncidentList({
  incidents,
  selectedId,
  onSelectIncident,
}: IncidentListProps) {
  // Sort by urgency only
  const sorted = [...incidents].sort((a, b) => {
    const aUrgent = a.status === "IN_PROGRESS" ? 0 : 1;
    const bUrgent = b.status === "IN_PROGRESS" ? 0 : 1;
    return aUrgent - bUrgent;
  });

  return (
    <div className="space-y-2 overflow-y-auto">
      {sorted.map((incident) => {
        const isSelected = incident.incidentId === selectedId;
        const isInProgress = incident.status === "IN_PROGRESS";

        return (
          <button
            key={incident.incidentId}
            onClick={() => onSelectIncident(incident.incidentId)}
            className={`w-full text-left p-4 rounded-lg transition-all border-2 ${
              isSelected
                ? "border-zinc-400 bg-zinc-50"
                : "border-zinc-200 bg-white hover:border-zinc-300"
            }`}
          >
            <div className="flex items-start justify-between gap-2 mb-2">
              <span className="text-sm font-bold text-zinc-900">
                #{incident.incidentId}
              </span>
              <span
                className={`px-3 py-1 text-xs font-semibold rounded-full ${
                  isInProgress
                    ? "bg-blue-100 text-blue-800"
                    : "bg-green-100 text-green-800"
                }`}
              >
                {isInProgress ? "IN PROGRESS" : "RESOLVED"}
              </span>
            </div>
            <div className="text-sm font-semibold text-zinc-900 mb-1">
              {incident.patientName}
            </div>
            <div className="text-xs text-zinc-600 mb-2">
              {incident.reportedIssue}
            </div>

          </button>
        );
      })}
    </div>
  );
}
