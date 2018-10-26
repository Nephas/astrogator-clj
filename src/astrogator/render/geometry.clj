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
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (q/fill color (* mag 192))
     (q/with-stroke [color (* mag 128)]
                    (do (q/stroke-weight (* size 2))
                        (circle pos size))))))

(defn shadow [pos phase size length]
  (q/with-translation pos
                      (q/fill (u/vec-to-color conf/planet-shade-color))
                      (q/rotate phase)
                      (q/rect 0 (* -1 size) length (* 2 size))
                      (q/rotate 0)))

(defn half-circle
  ([pos size phase-in color]
   (q/fill color)
   (q/no-stroke)
   (let [phase (+ Math/PI phase-in)
         rot (/ Math/PI 2)]
     (q/arc (pos 0) (pos 1) size size (- phase rot) (+ phase rot) :pie))))

(defn planet
  ([pos size phase color]
   (q/no-stroke)
   (shadow pos phase size (astrogator.conf/screen-size 0))
   (circle pos size (u/vec-to-color conf/planet-night-color))
   (half-circle pos size phase color)))