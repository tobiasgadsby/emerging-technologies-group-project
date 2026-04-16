package com.emergingtech;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class ResourceMapping {
    private IFn positions;
    public ResourceMapping() {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("jamescrake-merani.resource-mapping"));
        positions = Clojure.var("jamescrake-merani.resource-mapping", "positions");
    }
}
