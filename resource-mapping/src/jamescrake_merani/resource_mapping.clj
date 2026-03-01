(ns jamescrake-merani.resource-mapping
  (:require [loom.graph :as lg]
            [loom.alg :as la])
  (:gen-class))

(def example-road-map
  (lg/weighted-graph 
   {:a {:b 14 :d 12 :e 13}
    :b {:a 14 :c 16 :e 15 :f 12}
    :c {:b 16 :f 20 :g 12}
    :d {:a 12 :e 5  :h 12}
    :e {:a 13 :b 15 :d 5  :f 9}
    :f {:b 12 :c 20 :e 9  :g 16 :i 12}
    :g {:c 12 :f 16 :i 20 :j 12}
    :h {:d 12 :i 14}
    :i {:f 12 :g 20 :h 14 :j 16}
    :j {:g 12 :i 16}}))

;; destination-node can be nil if the agent isn't currently travelling
;; somewhere. In that case progress can be set to 0, but in reality it will be
;; ignored.
(defrecord Coordinate [current-node destination-node progress])

(def positions
  {:hospital [(Coordinate. :e nil 0)]
   :ambulance [(Coordinate. :b :c 5)]})

(defn nearest [graph current-position items]
  (apply min-key second (map (partial la/dijkstra-path-dist graph) (repeat (count items) current-position) items)))

;; TODO: At the moment, just consider where the ambulance is, not where its going to.
(defn dispatch [graph patient-location]
  (let [nearest-hospital (nearest graph patient-location (map :current-node (:hospital positions)))
        nearest-ambulance (nearest graph patient-location (map :current-node (:ambulance positions)))]
    {:eta (+ (second nearest-hospital) (second nearest-ambulance))
     :nearest-ambulance (last (first nearest-ambulance))
     :nearest-hospital (last (first nearest-hospital))}))
