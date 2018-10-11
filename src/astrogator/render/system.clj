(ns astrogator.render.system
  (:require [quil.core :as q]
            [astrogator.physics.trafo :as t]))

(defn get-stars [system]
  (if (nil? (get system :star))
    (flatten [(get-stars (system :compA)) (get-stars (system :compB))])
    [(get system :star)]))

(defn draw-stars [system camera]
  (doseq [star (get-stars system)]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* (camera :obj-zoom) (star :radius))]
      (do (q/fill (star :color))
          (q/ellipse (pos 0) (pos 1) size size)))))
