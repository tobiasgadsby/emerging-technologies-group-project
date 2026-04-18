"use client";

import { useState } from "react";
import { IncidentList } from "@/components/IncidentList";
import { IncidentDetail } from "@/components/IncidentDetail";
import { Incident } from "@/types/incident";

const MOCK_INCIDENTS: Incident[] = [
{
    incidentId: 42847,
    patientId: 1024,
    patientName: "John Smith",
    practitionerId: 5031,
    status: "IN_PROGRESS",
    reportedIssue: "Chest Pain",
    mlSummary:
        "Blah blah blah blah.",
    confidence: 94,
    transcript:
        '"I have chest pain"',
    audioDuration: "1m 12s",
    receivedAt: "2026-04-16T10:49:39.000Z",
},
{
    incidentId: 42846,
    patientId: 2015,
    patientName: "Bob Bob",
  practitionerId: 5032,
    status: "IN_PROGRESS",
    reportedIssue: "Dizziness",
    mlSummary:
        "Vertigo with nausea. Patient reports spinning sensation.",
    confidence: 78,
    transcript:
        '"I feel like I keep spinning. I feel nauseous too."',
    audioDuration: "47s",
    receivedAt: "2026-04-16T10:39:39.000Z",
},
{
    incidentId: 42845,
    patientId: 1847,
    patientName: "Charith IOT",
  practitionerId: 5033,
    status: "IN_PROGRESS",
    reportedIssue: "Nausea and Abdominal Discomfort",
    mlSummary:
        "Nausea with mild stomach discomfort.",
    confidence: 65,
    transcript:
        '"I feel really nauseous and my stomach hurts a bit.',
    audioDuration: "32s",
    receivedAt: "2026-04-16T10:29:39.000Z",
},
{
    incidentId: 42844,
    patientId: 2201,
    patientName: "Joan Smith",
  practitionerId: 5032,
    status: "RESOLVED",
    practitionerAction: "NO_ACTION",
    reportedIssue: "Headache",
    mlSummary:
      "Headache, moderate intensity. Localised at frontal region.",
    confidence: 72,
    transcript:
      '"I have a pretty bad headache behind my eyes."',
    audioDuration: "28s",
    receivedAt: "2026-04-16T10:09:39.000Z",
  },
  {
    incidentId: 42843,
    patientId: 1556,
    patientName: "Mr. E. Techologies",
    practitionerId: 5031,
    status: "RESOLVED",
    practitionerAction: "HOSPITAL_TRANSFER",
    reportedIssue: "Severe Back Pain",
    mlSummary:
      "Acute severe back pain (9/10).",
    confidence: 88,
    transcript:
      '"I felt something pop in my back when I bent down to pick something up. Now I can\'t move without excruciating pain."',
    audioDuration: "42s",
    receivedAt: "2026-04-16T09:54:39.000Z",
  },
];

const PRACTITIONER_IDS = Array.from(
  new Set(MOCK_INCIDENTS.map((incident) => incident.practitionerId))
).sort((left, right) => left - right);

type PractitionerSelection = number | "all";

export default function Dashboard() {
  const [selectedPractitionerId, setSelectedPractitionerId] = useState<PractitionerSelection>(
    "all"
  );
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [incidents, setIncidents] = useState<Incident[]>(MOCK_INCIDENTS);

  const visibleIncidents =
    selectedPractitionerId === "all"
      ? incidents
      : incidents.filter(
          (incident) => incident.practitionerId === selectedPractitionerId
        );

  const practitionerLabel =
    selectedPractitionerId === "all"
      ? "All practitioners"
      : `Practitioner #${selectedPractitionerId}`;

  const selectedIncident =
    visibleIncidents.find((incident) => incident.incidentId === selectedId) ||
    visibleIncidents[0] ||
    null;

  const handleAction = (action: "false-alarm" | "transfer") => {
    if (!selectedIncident) return;

    const updated = incidents.map((i) =>
      i.incidentId === selectedIncident.incidentId
        ? {
            ...i,
            status: "RESOLVED" as const,
            practitionerAction:
              action === "transfer"
                ? ("HOSPITAL_TRANSFER" as const)
                : ("NO_ACTION" as const),
          }
        : i
    );
    setIncidents(updated);

    const nextUrgent = updated.find(
      (i) =>
        i.status === "IN_PROGRESS" && i.incidentId !== selectedIncident.incidentId
    );
    if (nextUrgent) {
      setSelectedId(nextUrgent.incidentId);
    }
  };

  return (
    <div className="flex h-screen bg-zinc-50">
      <div className="w-80 border-r border-zinc-200 bg-white flex flex-col">
        <div className="border-b border-zinc-200 p-4 sticky top-0 bg-white">
          <h2 className="text-lg font-bold text-zinc-900">Your Incidents</h2>
          <div className="mt-3 space-y-2">
            <label className="block text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Practitioner ID
            </label>
            <select
              value={selectedPractitionerId}
              onChange={(event) =>
                setSelectedPractitionerId(
                  event.target.value === "all"
                    ? "all"
                    : Number(event.target.value)
                )
              }
              className="w-full rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 shadow-sm focus:border-zinc-500 focus:outline-none"
            >
              <option value="all">All practitioners</option>
              {PRACTITIONER_IDS.map((practitionerId) => (
                <option key={practitionerId} value={practitionerId}>
                  Practitioner #{practitionerId}
                </option>
              ))}
            </select>
          </div>
          <p className="text-xs text-zinc-600 mt-3">
            {visibleIncidents.length} incident{visibleIncidents.length !== 1 ? "s " : " "}
            for {practitionerLabel.toLowerCase()}
          </p>
        </div>
        <div className="flex-1 overflow-y-auto p-3">
          {visibleIncidents.length > 0 ? (
            <IncidentList
              incidents={visibleIncidents}
              selectedId={selectedIncident?.incidentId ?? null}
              onSelectIncident={setSelectedId}
            />
          ) : (
            <div className="rounded-lg border border-dashed border-zinc-300 bg-zinc-50 p-4 text-sm text-zinc-600">
              No incidents match this practitioner selection.
            </div>
          )}
        </div>
      </div>


      <div className="flex-1">
        <IncidentDetail incident={selectedIncident} onAction={handleAction} />
      </div>
    </div>
  );
}
