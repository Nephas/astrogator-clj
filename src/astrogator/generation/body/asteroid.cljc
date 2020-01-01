(ns astrogator.generation.body.asteroid
  (:require [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]
            [astrogator.render.draw.body :as draw]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]
            [astrogator.render.draw.geometry :as geo]))

(defrecord Asteroid [orbit mappos]
  orb/Orbit
  (orbit-move [this dt parent-mappos]
    (orb/move-around-parent this dt parent-mappos))

  draw/Drawable
  (draw-distant [this camera]
    (geo/particle (t/map-to-screen (:mappos this) camera) conf/particle-color))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (draw/draw-distant this camera))
  (draw-trail [this camera] nil))

(defn generate-asteroid [parent-mass orbit-radius]
  (let [orbit (o/circular-orbit parent-mass [orbit-radius nil])
        mappos [0 0]]
    (->Asteroid orbit mappos)))
