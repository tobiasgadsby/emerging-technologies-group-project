package com.emergingtech.orchestration.consumer;

import com.emergingtech.orchestration.db.model.Incident;
import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import com.emergingtech.orchestration.producer.PatientProducer;
import com.emergingtech.proto.Common.IncidentStatus;
import com.emergingtech.proto.Incident.IncidentRequest;
import com.emergingtech.proto.Incident.IncidentResponse;
import com.emergingtech.proto.Patient.PatientUpdate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.Base64;
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

    @Incoming("incident-request")
    public void incidentRequest(Map<String, Object> incident) {
        LOG.infof("Incident Request Received, Patient ID: %d", incident);
//        incidentDbService.createIncident(incidentMapper.mapIncidentRequestToIncidentEntity(incident));
    }

    @Incoming("incident-response")
    public void incidentResponse(IncidentResponse incident) {
        LOG.infof("Incident Response Received, Incident ID: %d", incident.getIncidentId());

        PatientUpdate.parseFrom(Base64.getDecoder().decode())
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
