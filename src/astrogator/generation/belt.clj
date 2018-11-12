(ns astrogator.generation.belt
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]))

(defn generate-particle [parent-mass orbit-radius]
  (let [torbit (a/t-orbit orbit-radius :AU parent-mass :Msol)]
    {:type   :asteroid
     :cylvel (* 2 Math/PI (/ 1 torbit))
     :cylpos [orbit-radius (* 2 Math/PI (r/rand))]
     :mappos [0 0]}))

(defn generate-asteroid-belt [parent-mass inner-radius outer-radius]
  (let [n-particles (r/rand-int-range 25 50)
        radii (map (fn [n] (r/rand-range (* 0.95 inner-radius) (* 1.05 outer-radius)))
                   (range n-particles))]
    (mapv #(generate-particle parent-mass %) radii)))