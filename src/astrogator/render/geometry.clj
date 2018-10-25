(ns astrogator.render.geometry
  (:require [quil.core :as q]
            [astrogator.util.log :as log]))

(defn circle
  ([pos size]
   (q/ellipse (pos 0) (pos 1) size size))
  ([pos size color]
   (q/fill color)
   (circle pos size)))

(defn star
  ([pos size color corona]
   (q/fill color)
   (q/with-stroke [color 128]
                  (do (q/stroke-weight (* size corona))
                      (circle pos size)))))

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

(defn cloud
  ([pos size color]
   (q/fill color 192)
   (q/with-stroke [color 128]
                  (do (q/stroke-weight (* size 2))
                      (circle pos size)))))

(defn planet
  ([pos size phase]
   (let [rot (/ Math/PI 2)]
     (q/arc (pos 0) (pos 1) size size (- phase rot) (+ phase rot) :pie)))
  ([pos size phase color]
   (q/fill color)
   (q/no-stroke)
   (planet pos size (+ Math/PI phase))))