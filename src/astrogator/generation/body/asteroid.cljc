(ns astrogator.generation.body.asteroid
  (:require [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]
            [astrogator.render.body.body :as draw]
            [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]))

(defrecord Asteroid [orbit mappos]
  orb/Orbit
  (orbit-move [this dt parent-mappos]
    (orb/move-around-parent this dt parent-mappos))

  draw/Drawable
  (draw-distant [this camera]
    (draw/particle (t/map-to-screen (:mappos this) camera) conf/particle-color))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (draw/draw-detail this camera)))

(defn generate-asteroid [parent-mass orbit-radius]
  (let [orbit (o/circular-orbit parent-mass [orbit-radius nil])
        mappos [0 0]]
    (->Asteroid orbit mappos)))
