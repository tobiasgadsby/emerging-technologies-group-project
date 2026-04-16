package com.emergingtech;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;

public class ResourceMapping {
    private IFn positions;
    private IFn graph;
    public ResourceMapping() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("jamescrake-merani.resource-mapping"));
        positions = Clojure.var("jamescrake-merani.resource-mapping", "positions");
        graph = Clojure.var("jamescrake-merani.resource-mapping", "graph");
    }
    private void dispatchAmbulance(String patientLocation) {
        IFn keyword = Clojure.var("clojure.core", "keyword");
        Object patientLocationKeyword = keyword.invoke(patientLocation);
        IFn dispatch = Clojure.var("jamescrake-merani.resource-mapping", "dispatch");
        PersistentArrayMap dispatchResult = (PersistentArrayMap)dispatch.invoke(graph, positions, patientLocationKeyword);
        // TODO: Add return.
    }
}
