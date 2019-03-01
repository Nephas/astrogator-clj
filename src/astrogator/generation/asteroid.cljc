(ns astrogator.generation.asteroid
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]))

(defrecord Asteroid [type cylvel cylpos mappos]
  orb/Orbit (orbit [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos)))

(defn generate-asteroid [parent-mass orbit-radius]
  (let [torbit (a/t-orbit orbit-radius :AU parent-mass :Msol)
        cylvel (* 2 Math/PI (/ 1 torbit))
        cylpos [orbit-radius (* 2 Math/PI (r/uniform))]
        mappos [0 0]]
    (->Asteroid :asteroid cylvel cylpos mappos)))
