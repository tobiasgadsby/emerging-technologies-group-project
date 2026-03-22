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

;; TODO: Perhaps use memoize?
(defn nearest [graph current-position items]
  (apply min-key second (map (partial la/dijkstra-path-dist graph) (repeat (count items) current-position) items)))

(defn dispatch-ambulance [graph positions ambulance-to-dispatch location path-to-location]
  (let [path-to-hospital (la/dijkstra-path graph (:current-node ambulance-to-dispatch) location)
        new-ambulance (AmbulanceStatus.
                       (:current-node ambulance-to-dispatch)
                       :patient
                       (lg/weight graph (first path-to-hospital) (second path-to-hospital))
                       path-to-hospital)]
    (assoc positions :ambulance (conj (remove #(= % ambulance-to-dispatch) (:ambulance positions)) new-ambulance ))))

;; TODO: At the moment, just consider where the ambulance is, not where its going to.
(defn dispatch [graph patient-location]
  (let [nearest-hospital (nearest graph patient-location (:hospital positions))
        available-ambulances (filter #(= (:movement-status %) :random-walk) (:ambulance positions))
        [path-to-nearest-ambulance nearest-ambulance-location] (nearest graph patient-location (map #(:current-node %) available-ambulances))
        nearest-hopistal (first (filter #(= (:current-node %) nearest-ambulance-location) (:ambulance positions)))]
    {:eta (+ (second nearest-hospital) (second nearest-ambulance))
     :nearest-ambulance (last (first nearest-ambulance))
     :nearest-hospital nearest-hospital
     :new-positions (dispatch-ambulance graph
                                        positions
                                        nearest-hospital
                                        patient-location
                                        path-to-nearest-ambulance)}))

(defn calculate-updated-progress [graph ticks-passed ambulance]
  (- (-> ambulance :coordinate :progress) ticks-passed))

(defn update-ambulance-random-walk [graph ticks-passed ambulance]
  (let [updated-progress (calculate-updated-progress graph ticks-passed ambulance)]
    (if (> updated-progress 0)
      (assoc ambulance :movement-progress updated-progress)
      (let [next-node (rand-nth (lg/out-edges graph (get-in ambulance :coordinate :current-node)))]
        (AmbulanceStatus.
         (first (:path ambulance))
         :random-walk
         (lg/weight (:current-node ambulance) next-node)
         (list next-node))))))

(defn ambulance-next-movement-status [current-movement-status]
  (if (= current-movement-status :patient)
    :hospital
    :random-walk))

(defn update-ambulance-progress-to-journey [graph ticks-passed ambulance]
  (let [updated-progress (calculate-updated-progress graph ticks-passed ambulance)]
    (if (> updated-progress 0)
      (assoc ambulance :movement-progress updated-progress)
      (let [next-node (journey-next-destination graph ambulance)]
        (if (nil? (first (:path ambulance)))
          (assoc ambulance :movement-status (ambulance-next-movement-status))
          (-> ambulance
              (assoc :current-node (first (:path ambulance)))
              (assoc :path (rest (:path ambulance)))))))))

(defn update-ambulance [ticks-passed ambulance]
  (if (= (:status ambulance) :random-walk)
    (update-ambulance-random-walk ticks-passed ambulance)
    (update-ambulance-progress-to-journey ticks-passed ambulance)))

(defn tick [graph positions ticks-passed]
  (assoc positions :ambulance (map (partial update-ambulance ticks-passed) (:ambulance positions))))
