(ns astrogator.generation.body.moon
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]
            [astrogator.render.draw.body :as draw]
            [quil.core :as q]
            [astrogator.render.draw.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]))

(defrecord Moon [mass radius color mappos])

(extend Moon orb/Orbit orb/orbit-impl)
(extend Moon draw/Drawable
  (merge draw/drawable-impl
         {:draw-detail (fn [this camera] (let [pos (t/map-to-screen (:mappos this) camera)
                                               size (* 0.1 (:radius this) (camera :obj-zoom))
                                               phase (:phase this)]
                                           (q/no-stroke)
                                           (geo/cast-shadow pos phase size (* 10 (q/width)))
                                           (geo/circle pos size (draw/main-color this))
                                           (geo/half-circle pos size phase conf/planet-night-color)))}))

(defn generate-moon [parent-mass orbit-radius]
  (let [mass (* 0.5 (r/uniform) (Math/log (+ parent-mass 1)))
        radius (a/planet-radius mass :Me)]
    (-> (->Moon mass radius conf/moon-surface-color [0 0])
        (o/init-orbit [parent-mass :Me] [orbit-radius nil] nil))))
