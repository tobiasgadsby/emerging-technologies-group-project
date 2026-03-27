package com.emergingtech.orchestration.consumer;

import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import com.emergingtech.proto.Incident.IncidentRequest;
import com.emergingtech.proto.Incident.IncidentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class IncidentConsumer {

    private static final Logger LOG = Logger.getLogger(IncidentConsumer.class);

    @Inject
    IncidentDbService incidentDbService;

    @Inject
    IncidentMapper incidentMapper;

    @Incoming("incident-request")
    public void incidentRequest(IncidentRequest incident) {
        LOG.infof("Incident Request Recieved, Patient ID: %d", incident.getPatientId());
        incidentDbService.createIncident(incidentMapper.mapIncidentRequestToIncidentEntity(incident));
    }

    @Incoming("incident-response")
    public void incidentResponse(IncidentResponse incident) {
        LOG.infof("Incident Response Received, Incident ID: %d", incident.getIncidentId());

    }

}
