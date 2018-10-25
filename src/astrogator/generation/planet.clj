(ns astrogator.generation.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [quil.core :as q]))

(defn generate-planet [parent-mass mass orbit-radius]
  (let [radius (a/mass-radius mass)
        torbit (a/t-orbit-d orbit-radius parent-mass)]
    {:mass   mass
     :radius radius
     :torbit torbit,
     :cylvel (* 2 Math/PI (/ 1 torbit)),
     :cylpos [orbit-radius 0]
     :color  (q/color 128 128 128)
     :mappos [0 0]}))

(defn generate-planet-system [parent-mass inner-radius outer-radius]
  (let [radii (into []
                    (filter #(< % outer-radius)
                            (map #(a/titius-bode % inner-radius) (range 10))))]
    (map #(generate-planet parent-mass (rand) %) radii)))