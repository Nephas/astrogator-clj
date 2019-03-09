(ns astrogator.generation.asteroid
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]))

(defrecord Asteroid [orbit mappos]
  orb/Orbit (orbit [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos)))

(defn generate-asteroid [parent-mass orbit-radius]
  (let [orbit (o/circular-orbit parent-mass [orbit-radius nil])
        mappos [0 0]]
    (->Asteroid orbit mappos)))
