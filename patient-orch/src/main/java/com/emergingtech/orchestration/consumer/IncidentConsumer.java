package com.emergingtech.orchestration.consumer;

import com.emergingtech.orchestration.db.common.IncidentStatus;
import com.emergingtech.orchestration.db.model.Incident;
import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import com.emergingtech.orchestration.producer.PatientProducer;
import com.emergingtech.proto.Incident.IncidentRequest;
import com.emergingtech.proto.Incident.IncidentResponse;
import com.emergingtech.proto.Patient.PatientUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class IncidentConsumer {

    private static final Logger LOG = Logger.getLogger(IncidentConsumer.class);

    @Inject
    IncidentDbService incidentDbService;

    @Inject
    IncidentMapper incidentMapper;

    @Inject
    PatientProducer patientProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    @Incoming("incident-request")
    public void incidentRequest(String incident) throws JsonProcessingException {

        Map<String, Object> payload = objectMapper.readValue(incident, Map.class);

        IncidentRequest incidentRequest = IncidentRequest.newBuilder().setPatientId((Integer) payload.get("patient_id")).setPractitionerId(1).setText((String)payload.get("transcript")).build();

        LOG.infof("Incident Request Received, Patient ID: %d", incidentRequest.getPatientId());
        incidentDbService.createIncident(incidentMapper.mapIncidentRequestToIncidentEntity(incidentRequest));
    }

    @Incoming("incident-response")
    public void incidentResponse(IncidentResponse incident) {
        LOG.infof("Incident Response Received, Incident ID: %d", incident.getIncidentId());
        incidentDbService.updateIncident(Incident.builder()
                        .incidentId(incident.getIncidentId())
                        .status(incident.getIncidentStatus().name())
                .build());
        patientProducer.updatePatient(PatientUpdate.newBuilder()
                        .setPatientId(incident.getPatientId())
                        .setIncidentStatus(incident.getIncidentStatus())
                .build());
    }

}
