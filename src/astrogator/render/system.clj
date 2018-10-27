(ns astrogator.render.system
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.gui.camera :as cam]))

(defn get-stars [system]
  (if (nil? (system :body))
    (flatten [(get-stars (system :compA)) (get-stars (system :compB))])
    [(system :body)]))

(defn get-planets [system]
  (if (nil? (system :compB))
    (system :planets)
    (flatten [(get-planets (system :compA)) (get-planets (system :compB)) (system :planets)])))

(defn draw-system [system camera]
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size 0.5]
      (geo/airy pos size (planet :color))))
  (doseq [star (get-stars system)]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* 5 (camera :obj-zoom) (star :radius))]
      (geo/star pos size (star :color) 0.4))))

(defn draw-subsystems [system camera]
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size (* (camera :obj-zoom) 1)]
      (geo/half-circle pos size (get-in planet [:cylpos 1]) (planet :color))))
  (doseq [star (get-stars system)]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* 5 (camera :obj-zoom) (star :radius))]
      (geo/star pos size (star :color) 0.4))))

(defn draw-refbody [system camera]
  (let [refbody (cam/get-refbody camera system)
        pos (t/map-to-screen (refbody :mappos) camera)
        color (refbody :color)]
    (case (refbody :type)
      :planet (geo/planet pos (camera :obj-zoom) (get-in refbody [:cylpos 1]) color)
      :star (geo/star pos (* 5 (camera :obj-zoom) (refbody :radius)) color 0.4))))