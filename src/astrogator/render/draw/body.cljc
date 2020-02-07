(ns astrogator.render.draw.body
  (:require [astrogator.render.draw.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]
            [astrogator.physics.trail :as trail]))

(defprotocol Drawable "draw the object given camera transformation data"
  (draw-detail [this camera] "high detail view")
  (draw-surface [this camera] "draw the hexmap surface")
  (draw-distant [this camera] "very low detail view (< 5 primitives)")
  (draw-trail [this camera] "draw a dotted trail from the trail array"))

(def drawable-impl
  {:draw-detail  (fn [this camera] (geo/particle (t/map-to-screen (:mappos this) camera) conf/particle-color))
   :draw-surface (fn [this camera] nil)
   :draw-distant (fn [this camera] (geo/particle (t/map-to-screen (:mappos this) camera) conf/particle-color))
   :draw-trail   (fn [this camera] (trail/draw-trail this camera conf/particle-color))})