"use client";

import { useState } from "react";
import { Incident } from "@/types/incident";

interface IncidentDetailProps {
  incident: Incident | null;
  onAction?: (action: "false-alarm" | "transfer") => void;
}

export function IncidentDetail({ incident, onAction }: IncidentDetailProps) {
  const [showConfirm, setShowConfirm] = useState<"false-alarm" | "transfer" | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  if (!incident) {
    return (
      <div className="flex items-center justify-center h-full text-zinc-500">
        Select an incident to view details
      </div>
    );
  }

  const handleAction = (action: "false-alarm" | "transfer") => {
    setIsLoading(true);
    setTimeout(() => {
      onAction?.(action);
      setShowConfirm(null);
      setIsLoading(false);
    }, 500);
  };

  return (
    <div className="flex flex-col h-full bg-white overflow-auto">
      {/* Header */}
      <div className="border-b border-zinc-200 bg-white sticky top-0 p-4">
        <div className="flex items-center justify-between mb-2">
          <h1 className="text-xl font-bold text-zinc-900">
            Incident #{incident.incidentId}
          </h1>
          <span
            className={`px-3 py-1 text-xs font-semibold rounded-full ${
              incident.status === "IN_PROGRESS"
                ? "bg-blue-100 text-blue-800"
                : "bg-green-100 text-green-800"
            }`}
          >
            {incident.status === "IN_PROGRESS" ? "IN PROGRESS" : "RESOLVED"}
          </span>
        </div>
        <p className="text-sm text-zinc-600">
          Patient: <span className="font-semibold">{incident.patientName}</span> (ID:{" "}
          {incident.patientId}) | Received: <span className="font-semibold">{incident.receivedAt}</span>
        </p>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto p-6 space-y-5">
        {/* Reported Issue */}
        <section>
          <h2 className="text-xs font-bold text-zinc-600 uppercase tracking-wide mb-2">
            Reported Issue
          </h2>
          <div className="bg-zinc-50 p-3 rounded border border-zinc-200">
            <p className="text-lg font-semibold text-zinc-900">
              {incident.reportedIssue}
            </p>
          </div>
        </section>

        {/* ML Assessment */}
        <section>
          <h2 className="text-xs font-bold text-zinc-600 uppercase tracking-wide mb-2">
            ML Assessment
          </h2>
          <div className="bg-zinc-50 p-3 rounded border border-zinc-200">
            <p className="text-sm text-zinc-800 leading-relaxed mb-3">
              {incident.mlSummary}
            </p>
            <div className="pt-3 border-t border-zinc-200">
              <p className="text-xs font-medium text-zinc-700">
                Confidence:{" "}
                <span className="font-bold text-zinc-900">{incident.confidence}%</span>
              </p>
            </div>
          </div>
        </section>

        {/* Decision Buttons */}
        {incident.status === "IN_PROGRESS" && (
          <section>
            <h2 className="text-xs font-bold text-zinc-600 uppercase tracking-wide mb-2">
              Your Decision
            </h2>
            {showConfirm ? (
              <div className="bg-yellow-50 border-2 border-yellow-300 rounded p-4">
                <p className="text-sm font-semibold text-yellow-900 mb-3">
                  {showConfirm === "transfer"
                    ? "Confirm urgent hospital transfer?"
                    : "Confirm false alarm?"}
                </p>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleAction(showConfirm)}
                    disabled={isLoading}
                    className="flex-1 py-2 bg-yellow-600 text-white font-semibold rounded hover:bg-yellow-700 disabled:opacity-50 transition"
                  >
                    {isLoading ? "..." : "Confirm"}
                  </button>
                  <button
                    onClick={() => setShowConfirm(null)}
                    disabled={isLoading}
                    className="flex-1 py-2 bg-zinc-200 text-zinc-900 font-semibold rounded hover:bg-zinc-300 disabled:opacity-50 transition"
                  >
                    Back
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex gap-2">
                <button
                  onClick={() => setShowConfirm("false-alarm")}
                  className="flex-1 py-3 bg-zinc-200 text-zinc-900 font-semibold rounded hover:bg-zinc-300 transition"
                >
                  No Action / False Alarm
                </button>
                <button
                  onClick={() => setShowConfirm("transfer")}
                  className="flex-1 py-3 bg-red-600 text-white font-semibold rounded hover:bg-red-700 transition"
                >
                  Transfer
                </button>
              </div>
            )}
          </section>
        )}

        {/* Audio & Transcript */}
        <section>
          <h2 className="text-xs font-bold text-zinc-600 uppercase tracking-wide mb-2">
            Audio & Transcript
          </h2>
          <div className="bg-zinc-50 p-3 rounded border border-zinc-200 mb-3">
            <div className="flex items-center justify-between">
              <p className="text-sm text-zinc-700">
                Duration: <span className="font-semibold">{incident.audioDuration}</span>
              </p>
              <button className="px-3 py-1 bg-zinc-900 text-white text-xs font-semibold rounded hover:bg-zinc-800 transition">
                PLAY
              </button>
            </div>
          </div>
          <div className="bg-zinc-50 p-3 rounded border border-zinc-200">
            <p className="text-sm text-zinc-800 leading-relaxed whitespace-pre-wrap">
              {incident.transcript}
            </p>
          </div>
        </section>
      </div>
    </div>
  );
}
