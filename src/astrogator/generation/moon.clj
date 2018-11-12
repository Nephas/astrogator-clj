(ns astrogator.generation.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]))

(defn generate-moon [parent-mass orbit-radius]
  (let [mass (r/rand-range 0.05 0.5)
        radius (a/planet-radius mass :Me)
        torbit (a/t-orbit orbit-radius :AU parent-mass :Me)]
    {:type   :moon
     :mass   mass
     :radius radius
     :torbit torbit
     :cylvel (* 2 Math/PI (/ 1 torbit))
     :cylpos [orbit-radius (* 2 Math/PI (r/rand))]
     :color  [128 128 128]
     :mappos [0 0]}))

(defn generate-moon-system [parent-mass inner-radius outer-radius]
  (let [n-moons (r/rand-int-range 0 5)
        radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius)
                            (range n-moons)))]
    (mapv #(generate-moon parent-mass %) radii)))