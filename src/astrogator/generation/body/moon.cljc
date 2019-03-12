(ns astrogator.generation.body.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]))

(defrecord Moon [mass radius orbit color mappos]
  orb/Orbit (orbit-move [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos)))

(defn generate-moon [parent-mass orbit-radius]
  (let [mass (* parent-mass (r/uniform 0.02 0.2))
        radius (a/planet-radius mass :Me)
        orbit (o/circular-orbit parent-mass :Me [orbit-radius nil])
        mappos [0 0]
        color [128 128 128]]
    (->Moon mass radius orbit color mappos)))
