(ns jamescrake-merani.resource-mapping-test
  (:require [clojure.test :refer [deftest is testing]]
            [jamescrake-merani.resource-mapping :as sut])) ; system under test

(deftest dispatch-test
  (testing "Tests dispatch reports the right eta."
    (is (= (:eta (sut/dispatch sut/example-road-map :c)) 28))))
