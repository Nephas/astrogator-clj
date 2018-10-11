(ns astrogator.physics.astro-test
  (:require [clojure.test :refer :all])
  (:require [astrogator.physics.astro :refer :all])
  (:require [astrogator.util.test :refer :all]))

(deftest t-orbit-d-test
  (testing "orbital time calculations"
    (doseq [data [{:radius  0.389 :mass 1
                   :t-orbit 88}
                  {:radius  1 :mass 1
                   :t-orbit 365}
                  {:radius  5.2 :mass 1
                   :t-orbit (* 365 11.8)}]]
      (is (close-to (data :t-orbit) (t-orbit-d (data :radius) (data :mass)))))))