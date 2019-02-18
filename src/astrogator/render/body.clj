(ns astrogator.render.body
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]
            [astrogator.render.field :as f]
            [astrogator.render.planet :as p]
            [astrogator.physics.trafo :as t]))

(defn cast-shadow [pos phase size length]
  (col/fill conf/planet-shade-color 196)
  (q/no-stroke)
  (q/with-translation pos
                      (q/with-rotation [(+ Math/PI phase)]
                                       (q/rect 0 (* -1 size) length (* 2 size)))))

(defn moon-with-shade
  ([pos size phase color]
   (q/no-stroke)
   (cast-shadow pos phase size (astrogator.conf/screen-size 0))
   (geo/circle pos size [0 0 128])
   (geo/half-circle pos size (+ Math/PI phase) conf/planet-night-color)))

(defn draw-planet [refbody camera]
  (doseq [moon (refbody :moons)]
    (let [pos (t/map-to-screen (moon :mappos) camera)
          size (* 0.1 (moon :radius) (camera :obj-zoom))
          phase (get-in refbody [:cylpos 1])]
      (moon-with-shade pos size phase (moon :color))))
  (let [pos (t/map-to-screen (refbody :mappos) camera)
        size (* 0.1 (refbody :radius) (camera :obj-zoom))
        phase (+ Math/PI (get-in refbody [:cylpos 1]))]
    (do (f/draw-soi refbody camera)
        (cast-shadow pos phase size (astrogator.conf/screen-size 0))
        (geo/circle pos size [64 64 96])
        (p/draw-surface (vals (refbody :surface)) (* 0.58 size))
        (geo/half-circle pos size phase conf/planet-night-color))))

(defn draw-star
  ([pos size color]
   (col/fill color)
   (q/with-stroke [(col/vec-to-color color) 128]
                  (do (q/stroke-weight (* size 0.4))
                      (geo/circle pos size))))
  ([star camera]
   (let [pos (t/map-to-screen (star :mappos) camera)
         size (* 5 (camera :obj-zoom) (star :radius))
         color (star :color)]
     (draw-star pos size color))))

(defn cloud
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (col/fill color (* mag 192))
     (q/with-stroke [(col/vec-to-color color) (* mag 128)]
                    (do (q/stroke-weight (* size 2))
                        (geo/circle pos size))))))

(defn distant-planet
  ([pos size phase color]
   (if (< size conf/airy-threshold)
     (geo/airy pos 1 color)
     (geo/half-circle pos size phase color))))

(defn distant-star
  ([pos size color]
   (col/fill color)
   (if (< size conf/airy-threshold)
     (geo/airy pos 2 color)
     (draw-star pos size color))))
