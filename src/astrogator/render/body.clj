(ns astrogator.render.body
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]
            [astrogator.util.util :as u]
            [astrogator.physics.trafo :as t]))

(defn star
  ([star camera]
   (let [pos (t/map-to-screen (star :mappos) camera)
         size (* 5 (camera :obj-zoom) (star :radius))
         color (star :color)
         corona 0.4]
     (q/fill color)
     (if (< size 3)
       (geo/airy pos 2 color)
       (q/with-stroke [color 128]
                      (do (q/stroke-weight (* size corona))
                          (geo/circle pos size)))))))

(defn cloud
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (q/fill color (* mag 192))
     (q/with-stroke [color (* mag 128)]
                    (do (q/stroke-weight (* size 2))
                        (geo/circle pos size))))))

(defn shadow [pos phase size length]
  (q/with-translation pos
                      (q/fill (u/vec-to-color conf/planet-shade-color) 196)
                      (q/rotate phase)
                      (q/rect 0 (* -1 size) length (* 2 size))
                      (q/rotate 0)))

(defn planet
  ([pos size phase color]
   (q/no-stroke)
   (shadow pos phase size (astrogator.conf/screen-size 0))
   (geo/circle pos size (u/vec-to-color conf/planet-night-color))
   (geo/half-circle pos size phase color)))