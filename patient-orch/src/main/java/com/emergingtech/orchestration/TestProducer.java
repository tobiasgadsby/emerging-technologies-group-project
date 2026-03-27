package com.emergingtech.orchestration;

import com.emergingtech.proto.Incident;
import com.emergingtech.proto.Incident;
import com.emergingtech.proto.Incident.IncidentRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.Properties;

@Path("/test-incident")
public class TestProducer {

    @Inject
    @Channel("incident-request")
    Emitter<Incident.IncidentRequest> incidentRequestEmitter;

    @POST
    public void sendIncident() {

        IncidentRequest incidentRequest = IncidentRequest.newBuilder().setPatientId(1L).setPractitionerId(1L).build();

        incidentRequestEmitter.send(incidentRequest);

    }


}
