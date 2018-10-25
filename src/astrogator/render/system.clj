(ns astrogator.render.system
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
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
      (geo/star pos size (star :color) 0.4)))
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size (* (camera :obj-zoom) 1)]
      (geo/planet pos size (get-in planet [:cylpos 1]) (planet :color)))))

