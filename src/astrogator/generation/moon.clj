(ns astrogator.generation.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.util :as u]))

(defn generate-moon [parent-mass mass orbit-radius]
  (let [radius (a/mass-radius mass)
        torbit (a/t-orbit-d orbit-radius parent-mass)]
    {:type   :moon
     :mass   mass
     :radius radius
     :torbit torbit,
     :cylvel (* 2 Math/PI (/ 1 torbit)),
     :cylpos [orbit-radius 0]
     :color  (u/vec-to-color [128 128 128])
     :mappos [0 0]}))

(defn generate-moon-system [parent-mass inner-radius outer-radius]
  (let [radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius) (range 10)))]
    (mapv #(generate-moon parent-mass (* 0.1 (rand)) %) radii)))