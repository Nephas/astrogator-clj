(ns astrogator.render.system
  (:require [quil.core :as q]
            [astrogator.physics.trafo :as t]))

(defn get-stars [system]
  (if (nil? (system :star))
    (flatten [(get-stars (system :compA)) (get-stars (system :compB))])
    [(system :star)]))

(defn get-planets [system]
  (if (nil? (system :compB))
    (system :planets)
    (flatten [(get-planets (system :compA)) (get-planets (system :compB)) (system :planets)])))

(defn draw-system [system camera]
  (doseq [star (get-stars system)]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* (camera :obj-zoom) (star :radius))]
      (do (q/fill (star :color))
          (q/ellipse (pos 0) (pos 1) size size))))
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size 2]
      (do (q/fill 255 255 255)
          (q/ellipse (pos 0) (pos 1) size size)))))

