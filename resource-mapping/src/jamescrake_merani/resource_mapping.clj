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

(defrecord AmbulanceStatus [current-node movement-status movement-progress path])
;; movement-progress can be :stationary if the ambulance is not moving.
;; If the path is empty, use the movement status to work out what to do next.
(def ambulance-movement-status #{:random-walk :patient :hospital})

(def positions
  {:hospital [:b :i]
   :ambulance [(AmbulanceStatus. :d :random-walk 2 '(:e))
               (AmbulanceStatus. :g :random-walk 5 '(:c))]})

(defn nearest [graph current-position items]
  "Works out the position in `items` which is nearest to `current-position` in `graph`"
  (apply min-key second (map (partial la/dijkstra-path-dist graph) (repeat (count items) current-position) items)))

(defn dispatch-ambulance [graph current-positions ambulance-to-dispatch location path-to-location]
  "Dispatch `ambulance-to-dispatch` to `current-positions`.
Return value is a copy of `current-positions` updated with the ambulance being
dispatched"
  (let [path-to-hospital
        (la/dijkstra-path graph (:current-node ambulance-to-dispatch) location)
        new-ambulance (AmbulanceStatus. (:current-node ambulance-to-dispatch) :patient
                                        (lg/weight graph (first path-to-hospital) (second path-to-hospital))
                                        path-to-hospital)] (assoc current-positions :ambulance (conj (remove #(= %
                                                                                                                 ambulance-to-dispatch) (:ambulance current-positions)) new-ambulance ))))

(defn dispatch [graph current-positions patient-location]
  "Dispatch an ambulance (from `current-position$i) to `patient-location`, working out the closest ambulance that will get the patient to the hospital as quickly as possible."
  (let [[path-to-nearest-hospital nearest-hospital-distance] (nearest graph patient-location (:hospital positions))
        available-ambulances (filter #(= (:movement-status %) :random-walk) (:ambulance positions))
        [path-to-nearest-ambulance nearest-ambulance-distance] (nearest graph patient-location (map #(:current-node %) available-ambulances))
        nearest-ambulance (first (filter #(= (:current-node %) (last path-to-nearest-ambulance)) (:ambulance positions)))]
    {:eta (+ nearest-hospital-distance nearest-ambulance-distance)
     :nearest-ambulance (last (first nearest-ambulance))
     :nearest-hospital (last path-to-nearest-hospital)
     :new-positions (dispatch-ambulance graph
                                        current-positions
                                        nearest-ambulance
                                        patient-location
                                        path-to-nearest-ambulance)}))

(defn calculate-updated-progress [ticks-passed ambulance]
  "Calculate the amount of progress `ambulance` is to its destination based on the amount of time that has passed (`ticks-passed`)"
  (- (:movement-progress ambulance) ticks-passed))

(defn update-ambulance-random-walk [graph ticks-passed ambulance]
  "Update the random walk of `ambulance`"
  (let [updated-progress (calculate-updated-progress ticks-passed ambulance)]
    (if (> updated-progress 0)
      (assoc ambulance :movement-progress updated-progress)
      (let [next-node (rand-nth (map second (lg/out-edges graph (:current-node ambulance))))]
        (update-ambulance-random-walk
         graph
         (abs updated-progress)
         (AmbulanceStatus.
          (first (:path ambulance))
          :random-walk
          (lg/weight graph (:current-node ambulance) next-node)
          (list next-node)))
        ))))

(defn ambulance-next-movement-status [current-movement-status]
  "Based on `current-movement-status`, work out the next movement status."
  (if (= current-movement-status :patient)
    :hospital
    :random-walk))

(defn update-ambulance-progress-to-journey [graph ticks-passed ambulance]
  "Calculate how much progress has been made to the destination of `ambulance` based on how much time has passed (`ticks-passed`). If the ambulance has reached their journey, work out the next status for it."
  (let [updated-progress (calculate-updated-progress graph ticks-passed ambulance)]
    (if (> updated-progress 0)
      (assoc ambulance :movement-progress updated-progress)
      (if (nil? (first (:path ambulance)))
        (assoc ambulance :movement-status (ambulance-next-movement-status))
        (-> ambulance
            (assoc :current-node (first (:path ambulance)))
            (assoc :path (rest (:path ambulance))))))))

(defn update-ambulance [graph ticks-passed ambulance]
  "Update `ambulance`'s movement based on the amount of time that has passed (`ticks-passed`)."
  (if (= (:movement-status ambulance) :random-walk)
    (update-ambulance-random-walk graph ticks-passed ambulance)
    (update-ambulance-progress-to-journey graph ticks-passed ambulance)))

(defn tick [graph positions ticks-passed]
  "Update the state of the `positions` based on the amount of time that has progressed in the simulation (expressed as `ticks-passed`, where ticks is an arbitary unit). Returns the updated positions."
  (assoc positions :ambulance (map (partial update-ambulance graph ticks-passed) (:ambulance positions))))
