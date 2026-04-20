package com.emergingtech.orchestration.consumer;

import com.emergingtech.orchestration.service.ResourceOrchService;
import com.emergingtech.proto.ResourceMapper.PractitionerActionRequest;
import com.emergingtech.proto.ResourceMapper.ResourceMappingResponse;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

public class ResourceOrchConsumer {

    private static final Logger LOG = Logger.getLogger(ResourceOrchConsumer.class);

    @Inject
    ResourceOrchService resourceOrchService;

    @Incoming("action-request-in")
    public void actionRequest(PractitionerActionRequest practitionerActionRequest) {

        LOG.infof("Received action request, Incident ID: %s", practitionerActionRequest.getIncidentId());

        resourceOrchService.processActionRequest(practitionerActionRequest);

    }

    @Incoming("mapping-response")
    public void mappingResponse(ResourceMappingResponse resourceMappingResponse) {

        LOG.infof("Received mapping response, ID: %s", resourceMappingResponse.getMappingId());

        resourceOrchService.processMappingResponse(resourceMappingResponse);

    }

}
