(ns astrogator.render.body.body
  (:require [astrogator.util.color :as col]
            [astrogator.render.conf :as conf]
            [quil.core :as q]
            [astrogator.render.geometry :as geo]))

(defn particle [pos color]
    (q/no-stroke)
    (col/fill color)
    (geo/circle pos 1))

(defn cast-shadow [pos phase size length]
  (col/fill conf/planet-shade-color 128)
  (q/no-stroke)
  (q/with-translation pos
                      (q/with-rotation [(+ Math/PI phase)]
                                       (q/rect 0 (* -1 size) length (* 2 size)))))

(defprotocol Drawable "draw the object given camera transformation data"
  (draw-detail [this camera] "high detail view")
  (draw-surface [this camera] "draw the hexmap surface")
  (draw-distant [this camera] "very low detail view (< 5 primitives)"))
