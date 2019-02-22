(ns astrogator.generation.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]))

(defrecord Moon [type mass radius torbit cylvel cylpos color mappos]
  orb/Orbit (orbit [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos)))

(defn generate-moon [parent-mass orbit-radius]
  (let [mass (r/rand-range 0.05 0.5)
        radius (a/planet-radius mass :Me)
        torbit (a/t-orbit orbit-radius :AU parent-mass :Me)
        cylvel (* 2 Math/PI (/ 1 torbit))
        cylpos [orbit-radius (* 2 Math/PI (r/rand))]
        color [128 128 128]
        mappos [0 0]]
    (->Moon :type mass radius torbit cylvel cylpos color mappos)))
