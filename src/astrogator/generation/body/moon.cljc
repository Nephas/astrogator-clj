(ns astrogator.generation.body.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]
            [astrogator.render.body.body :as draw]
            [quil.core :as q]
            [astrogator.render.body.planet :as p]
            [astrogator.render.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]))

(defrecord Moon [mass radius orbit color mappos]
  orb/Orbit
  (orbit-move [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos))

  draw/Drawable
  (draw-distant [this camera]
    (draw/particle (t/map-to-screen (:mappos this) camera) conf/moon-surface-color))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (let [pos (t/map-to-screen (:mappos this) camera)
          size (* 0.1 (:radius this) (camera :obj-zoom))
          phase (:phase this)]
      (q/no-stroke)
      (draw/cast-shadow pos phase size (* 10 (q/width)))
      (geo/circle pos size conf/moon-surface-color)
      (geo/half-circle pos size phase conf/planet-night-color))))

(defn generate-moon [parent-mass orbit-radius]
  (let [mass (* 0.5 (r/uniform) (Math/log (+ parent-mass 1)))
        radius (a/planet-radius mass :Me)
        orbit (o/circular-orbit [parent-mass :Me] [orbit-radius nil] nil)
        mappos [0 0]
        color [128 128 128]]
    (->Moon mass radius orbit color mappos)))
