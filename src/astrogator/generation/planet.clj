(ns astrogator.generation.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.generation.moon :as m]
            [quil.core :as q]
            [astrogator.util.util :as u]))

(defn generate-planet [parent-mass mass orbit-radius]
  (let [radius (a/mass-radius mass)
        torbit (a/t-orbit-d orbit-radius parent-mass)]
    {:type   :planet
     :mass   mass
     :radius radius
     :torbit torbit,
     :cylvel (* 2 Math/PI (/ 1 torbit)),
     :cylpos [orbit-radius 0]
     :color  (u/vec-to-color [128 196 128])
     :mappos [0 0]
     :moons  (m/generate-moon-system 0.000001 0.0001 0.001)}))

(defn generate-planet-system [parent-mass inner-radius outer-radius]
  (let [radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius) (range 10)))]
    (mapv #(generate-planet parent-mass (rand) %) radii)))