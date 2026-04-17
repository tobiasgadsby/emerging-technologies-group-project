package com.emergingtech;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;

import java.time.Instant;

import com.emergingtech.proto.ResourceMapper;
import com.google.protobuf.Timestamp;

public class ResourceMapping {
    private IFn positions;
    private IFn graph;
    public ResourceMapping() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("jamescrake-merani.resource-mapping"));
        positions = Clojure.var("jamescrake-merani.resource-mapping", "positions");
        graph = Clojure.var("jamescrake-merani.resource-mapping", "graph");
    }
    public ResourceMapper.ResourceMappingResponse dispatchAmbulance(String patientLocation) {
        IFn keyword = Clojure.var("clojure.core", "keyword");
        Object patientLocationKeyword = keyword.invoke(patientLocation);
        IFn dispatch = Clojure.var("jamescrake-merani.resource-mapping", "dispatch");
        PersistentArrayMap dispatchResult = (PersistentArrayMap)dispatch.invoke(graph, positions, patientLocationKeyword);
        int etaMinutes = (int)dispatchResult.get("eta");
        Instant etaInst = Instant.now().plusSeconds(etaMinutes * 60);
        Timestamp etaProtoTimestamp = Timestamp.newBuilder().setSeconds(etaInst.getEpochSecond()).setNanos(etaInst.getNano()).build();
        ResourceMapper.ResourceMappingResponse response = ResourceMapper.ResourceMappingResponse.newBuilder()
            .setMappingId(1)
            .setArrivalTime(etaProtoTimestamp)
            .build();
        return response;
    }
}
