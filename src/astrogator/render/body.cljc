(ns astrogator.render.body
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]
            [astrogator.render.field :as f]
            [astrogator.render.planet :as p]
            [astrogator.physics.trafo :as t]))

(defn cast-shadow [pos phase size length]
  (col/fill conf/planet-shade-color 128)
  (q/no-stroke)
  (q/with-translation pos
                      (q/with-rotation [(+ Math/PI phase)]
                                       (q/rect 0 (* -1 size) length (* 2 size)))))

(defn moon-with-shade
  ([pos size phase]
   (q/no-stroke)
   (cast-shadow pos phase size (* 10 (q/width)))
   (geo/circle pos size conf/moon-surface-color)
   (geo/half-circle pos size phase conf/planet-night-color)))

(defn draw-planet [refbody camera]
  (doseq [moon (:moons refbody)]
    (let [pos (t/map-to-screen (:mappos moon) camera)
          size (* 0.1 (:radius moon) (camera :obj-zoom))
          phase (+ Math/PI (get-in refbody [:orbit :cylpos 1]))]
      (moon-with-shade pos size phase)))
  (let [pos (t/map-to-screen (:mappos refbody) camera)
        size (* 0.1 (:radius refbody) (camera :obj-zoom))
        phase (+ Math/PI (get-in refbody [:orbit :cylpos 1]))
        rot (get-in refbody [:rotation :angle])]
    (do (f/draw-soi refbody camera)
        (p/draw-surface (vals (:surface refbody)) (:color refbody) (* 0.65 size) rot)
        (geo/ring pos (* 1.1 size) conf/back-color (* 0.2 size))
        (cast-shadow pos phase size (* 10 (q/width)))
        (geo/half-circle pos size phase conf/planet-night-color))))

(defn draw-star
  ([pos size color]
   (col/fill color)
   (q/with-stroke [(apply q/color color) 128]
                  (do (q/stroke-weight (* size 0.4))
                      (geo/circle pos size))))
  ([star camera]
   (let [pos (t/map-to-screen (:mappos star) camera)
         size (* 5 (camera :obj-zoom) (:radius star))
         color (:color star)]
     (draw-star pos size color))))

(defn cloud
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (col/fill color (* mag 192))
     (q/with-stroke [(apply q/color color) (* mag 128)]
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
