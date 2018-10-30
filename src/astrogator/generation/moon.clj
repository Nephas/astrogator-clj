(ns astrogator.generation.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.util :as u]
            [astrogator.util.rand :as r]
            [astrogator.physics.units :as unit]))

(defn generate-moon [parent-mass-Me orbit-radius]
  (let [mass-Me (r/rand-range 0.05 0.5)
        radius-Re (a/planet-radius mass-Me)
        torbit (a/t-orbit-d orbit-radius (unit/conv parent-mass-Me :Me :Msol))]
    {:type   :moon
     :mass   mass-Me
     :radius radius-Re
     :torbit torbit
     :cylvel (* 2 Math/PI (/ 1 torbit))
     :cylpos [orbit-radius (* 2 Math/PI (r/rand))]
     :color  (u/vec-to-color [128 128 128])
     :mappos [0 0]}))

(defn generate-moon-system [parent-mass inner-radius outer-radius]
  (let [n-moons (r/rand-int-range 0 5)
        radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius)
                            (range n-moons)))]
    (mapv #(generate-moon parent-mass %) radii)))