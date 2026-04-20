package com.emergingtech;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;

import java.time.Instant;

import com.emergingtech.proto.ResourceMapper;
import com.emergingtech.proto.Common.Coordinates;
import com.emergingtech.proto.ResourceMapper.ResourceMappingRequest;
import com.emergingtech.proto.ResourceMapper.ResourceMappingResponse;
import com.google.protobuf.Timestamp;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

public class ResourceMapping {

    private static final Logger LOG = Logger.getLogger(ResourceMapping.class);

    private IFn positions;
    private IFn graph;

    @Channel("resource-response")
    Emitter<ResourceMappingResponse> emitter;

    public ResourceMapping() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("jamescrake-merani.resource-mapping"));
        positions = Clojure.var("jamescrake-merani.resource-mapping", "positions");
        graph = Clojure.var("jamescrake-merani.resource-mapping", "graph");
    }

    @Incoming("resource-request")
    public void resourceRequest(ResourceMappingRequest resourceMappingRequest) {
        LOG.infof("Resource Request Received, Mapping ID: %d", resourceMappingRequest.getMappingId());
        emitter.send(dispatchAmbulance(resourceMappingRequest));
    }


    public ResourceMapper.ResourceMappingResponse dispatchAmbulance(ResourceMapper.ResourceMappingRequest request) {
        Coordinates patientCoordinates =  request.getPatientCoordinates();
        int unpairedCoord = (int)(patientCoordinates.getX() + patientCoordinates.getY());
        char patientLocation = (char)('a' + unpairedCoord);
        IFn keyword = Clojure.var("clojure.core", "keyword");
        Object patientLocationKeyword = keyword.invoke(Character.toString(patientLocation));
        IFn dispatch = Clojure.var("jamescrake-merani.resource-mapping", "dispatch");
        PersistentArrayMap dispatchResult = (PersistentArrayMap)dispatch.invoke(graph, positions, patientLocationKeyword);
        int etaMinutes = (int)dispatchResult.get("eta");
        Instant etaInst = Instant.now().plusSeconds(etaMinutes * 60);
        Timestamp etaProtoTimestamp = Timestamp.newBuilder().setSeconds(etaInst.getEpochSecond()).setNanos(etaInst.getNano()).build();
        ResourceMapper.ResourceMappingResponse response = ResourceMapper.ResourceMappingResponse.newBuilder()
            .setMappingId(request.getMappingId())
            .setArrivalTime(etaProtoTimestamp)
            .build();
        return response;
    }
}
