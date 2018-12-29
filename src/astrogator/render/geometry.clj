(ns astrogator.render.geometry
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.physics.trafo :as t]))

(defn arrow [pos vec]
  (q/line pos (t/add pos vec)))

(defn circle
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color]
   (col/fill color)
   (circle pos size)))

(defn ring
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color]
   (col/fill [0 0 0] 0)
   (q/with-stroke [(col/vec-to-color color) 128]
                  (do (q/stroke-weight 2)
                      (ring pos size)))))

(defn airy
  ([pos size color]
   (q/with-stroke [(col/vec-to-color color) 96]
                  (do (col/fill color 255)
                      (q/stroke-weight (* 4 size))
                      (circle pos size)))
   (q/with-stroke [(col/vec-to-color color) 128]
                  (do (col/fill color 64)
                      (q/stroke-weight (* 1/2 size))
                      (circle pos (* 2 size))))))

(defn half-circle
  ([pos size phase-in color]
   (col/fill color)
   (q/no-stroke)
   (let [phase (+ Math/PI phase-in)
         rot (/ Math/PI 2)]
     (q/arc (pos 0) (pos 1) size size (- phase rot) (+ phase rot) :pie))))