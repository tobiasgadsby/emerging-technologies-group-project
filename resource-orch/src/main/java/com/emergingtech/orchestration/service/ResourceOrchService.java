package com.emergingtech.orchestration.service;

import com.emergingtech.orchestration.db.common.ResourceStatus;
import com.emergingtech.orchestration.db.model.Incident;
import com.emergingtech.orchestration.db.model.ResourceMapping;
import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.orchestration.db.service.ResourceMappingDbService;
import com.emergingtech.orchestration.producer.ResourceOrchProducer;
import com.emergingtech.proto.Common.Coordinates;
import com.emergingtech.proto.Common.IncidentStatus;
import com.emergingtech.proto.Incident.IncidentResponse;
import com.emergingtech.proto.ResourceMapper.PractitionerActionRequest;
import com.emergingtech.proto.ResourceMapper.ResourceMappingRequest;
import com.emergingtech.proto.ResourceMapper.ResourceMappingResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@ApplicationScoped
public class ResourceOrchService {

    @Channel("mapping-request")
    Emitter<ResourceMappingRequest> resourceMappingRequest;

    @Inject
    ResourceMappingDbService resourceMappingDbService;

    @Inject
    IncidentDbService incidentDbService;

    @Inject
    ResourceOrchProducer resourceOrchProducer;

    public void processActionRequest(PractitionerActionRequest practitionerActionRequest) {

        ResourceMapping resourceMapping = resourceMappingDbService.createResourceMapping(ResourceMapping.builder()
                .incidentId(practitionerActionRequest.getIncidentId())
                .status(ResourceStatus.UNMAPPED.toString())
                .build());

        Incident incident = incidentDbService.getIncidentById(practitionerActionRequest.getIncidentId());

        resourceMappingRequest.send(ResourceMappingRequest.newBuilder().setMappingId(resourceMapping.getMappingId()).setPatientCoordinates(Coordinates.newBuilder()
                        .setX(incident.getLocation().getX())
                        .setX(incident.getLocation().getY())
                .build()).build());

    }

    public void processMappingResponse(ResourceMappingResponse resourceMappingResponse) {

        ResourceMapping resourceMapping = resourceMappingDbService.getResourceMappingById(resourceMappingResponse.getMappingId());
        resourceMapping.setStatus(ResourceStatus.MAPPED.toString());
        resourceMapping.setEstimatedArrivalTime(Instant.ofEpochSecond(resourceMappingResponse.getArrivalTime().getSeconds(), resourceMapping.getEstimatedArrivalTime().getNano()).atZone(ZoneOffset.UTC).toLocalDateTime());
        resourceMappingDbService.updateResourceMapping(resourceMapping);
        resourceOrchProducer.sendIncidentResponse(IncidentResponse.newBuilder().setIncidentId(resourceMapping.getResourceId()).setIncidentStatus(IncidentStatus.valueOf(resourceMapping.getStatus())).build());

    }

}
