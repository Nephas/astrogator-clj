(ns astrogator.render.geometry
  (:require [quil.core :as q]
            [astrogator.render.conf :as conf]
            [astrogator.util.util :as u]))

(defn circle
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color]
   (q/fill color)
   (circle pos size)))

(defn airy
  ([pos size color]
   (q/with-stroke [color 96]
                  (do (q/fill color 255)
                      (q/stroke-weight (* 4 size))
                      (circle pos size)))
   (q/with-stroke [color 128]
                  (do (q/fill color 64)
                      (q/stroke-weight (* 1/2 size))
                      (circle pos (* 2 size))))))

(defn half-circle
  ([pos size phase-in color]
   (q/fill color)
   (q/no-stroke)
   (let [phase (+ Math/PI phase-in)
         rot (/ Math/PI 2)]
     (q/arc (pos 0) (pos 1) size size (- phase rot) (+ phase rot) :pie))))