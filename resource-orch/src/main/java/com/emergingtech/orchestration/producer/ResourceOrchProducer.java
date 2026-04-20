package com.emergingtech.orchestration.producer;

import com.emergingtech.proto.Incident.IncidentResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ResourceOrchProducer {

    @Channel("incident-response")
    Emitter<IncidentResponse> incidentResponseEmitter;

    public void sendIncidentResponse(IncidentResponse incidentResponse) {

        incidentResponseEmitter.send(incidentResponse);

    }


}
