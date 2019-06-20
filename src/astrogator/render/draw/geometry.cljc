(ns astrogator.render.draw.geometry
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]))

(defn arrow
  ([pos vec]
   (do (q/stroke-weight 1)
       (q/line pos (t/add pos vec))))
  ([pos vec scale] (arrow pos (t/scalar scale vec))))

(defn circle
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color]
   (col/fill color)
   (circle pos size)))

(defn ring
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color width]
   (col/fill)
   (q/with-stroke [(apply q/color color)]
                  (do (q/stroke-weight width)
                      (ring pos size))))
  ([pos size color]
   (ring pos size color 2)))

(defn airy
  ([pos size color]
   (q/with-stroke [(apply q/color color) 96]
                  (do (col/fill color 255)
                      (q/stroke-weight (* 4 size))
                      (circle pos size)))
   (q/with-stroke [(apply q/color color) 128]
                  (do (col/fill color 64)
                      (q/stroke-weight (* 0.5 size))
                      (circle pos (* 2 size))))))

(defn half-circle
  ([pos size phase-in color]
   (col/fill color)
   (q/with-stroke [(apply q/color color)]
                  (q/stroke-weight 2)
                  (let [phase (+ Math/PI phase-in)
                        rot (/ Math/PI 2)]
                    (q/arc (pos 0) (pos 1) size size (- phase rot) (+ phase rot))))))

(defn particle [pos color]
  (q/no-stroke)
  (col/fill color)
  (circle pos 1))

(defn cast-shadow [pos phase size length]
  (col/fill conf/planet-shade-color 128)
  (q/no-stroke)
  (q/with-translation pos
                      (q/with-rotation [(+ Math/PI phase)]
                                       (q/rect 0 (* -1 size) length (* 2 size)))))