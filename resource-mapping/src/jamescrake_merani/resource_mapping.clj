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
(defrecord AmbulanceStatus [coordinate status])

(def positions
  {:hospital [(Coordinate. :b nil 0)
              (Coordinate. :i nil 0 )]
   :ambulance [(AmbulanceStatus. (Coordinate. :d :e 2) :random-walk)
               (AmbulanceStatus. (Coordinate. :g nil 0) :random-walk)]})

(defn nearest [graph current-position items]
  (apply min-key second (map (partial la/dijkstra-path-dist graph) (repeat (count items) current-position) items)))

(defn dispatch-ambulance [graph positions ambulance-to-dispatch location]
  (let [path-to-hospital (la/dijkstra-path graph ambulance-to-dispatch location)
        new-ambulance (AmbulanceStatus.
                       (Coordinate. (-> ambulance-to-dispatch :coordinate :current-node)
                                    (first path-to-hospital)
                                    (lg/weight graph (-> ambulance-to-dispatch :coordinate :current) (:current-node location)))
                       path-to-hospital)]
    (assoc positions :ambulance (conj new-ambulance (remove #(= % ambulance-to-dispatch) (:ambulance positions))))))

;; TODO: At the moment, just consider where the ambulance is, not where its going to.
(defn dispatch [graph patient-location]
  (let [nearest-hospital (nearest graph patient-location (map :current-node (:hospital positions)))
        available-ambulances (filter #(= (:status %) :random-walk) (:ambulance positions))
        nearest-ambulance (nearest graph patient-location (map #(-> % :coordinate :current-node) available-ambulances))]
    {:eta (+ (second nearest-hospital) (second nearest-ambulance))
     :nearest-ambulance (last (first nearest-ambulance))
     :nearest-hospital (last (first nearest-hospital))
     :new-positions (dispatch-ambulance graph nearest-ambulance patient-location)}))

(defn calculate-updated-progress [graph ticks-passed ambulance]
  (- (-> ambulance :coordinate :progress) ticks-passed))

(defn update-ambulance-random-walk [graph ticks-passed ambulance]
  (let [updated-progress (calculate-updated-progress graph ticks-passed ambulance)]
    (if (> updated-progress 0)
      (assoc-in ambulance [:coordinate :progress] updated-progress)
      (let [next-node (rand-nth (lg/out-edges graph (get-in ambulance :coordinate :current-node)))]
        (AmbulanceStatus. (Coordinate. (get-in ambulance [:coordinate :destination-node])
                                       next-node
                                       (+ (lg/weight graph (get-in ambulance :coordinate :current-node) next-node) updated-progress))
                          :random-walk)))))

(defn update-ambulance-progress-to-journey [ticks-passed ambulance]
  (throw "Not implemented"))

(defn update-ambulance [ticks-passed ambulance]
  (if (= (:status ambulance) :random-walk)
    (update-ambulance-random-walk ticks-passed ambulance)
    (update-ambulance-progress-to-journey ticks-passed ambulance)))

(defn tick [graph positions ticks-passed]
  (assoc positions :ambulance (map (partial update-ambulance ticks-passed) (:ambulance positions))))
