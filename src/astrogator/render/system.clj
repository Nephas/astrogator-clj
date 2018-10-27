(ns astrogator.render.system
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.render.body :as b]
            [astrogator.physics.trafo :as t]
            [astrogator.gui.camera :as cam]
            [astrogator.util.log :as log]))

(defn get-bodies [system]
  (if (nil? (system :body))
    (flatten [(get-bodies (system :compA)) (get-bodies (system :compB))])
    [(system :body)]))

(defn get-planets [system]
  (if (nil? (system :body))
    (flatten [(get-planets (system :compA)) (get-planets (system :compB)) (system :planets)])
    (system :planets)))

(defn draw-system [system camera]
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)]
      (geo/airy pos 1 (planet :color))))
  (doseq [star (get-bodies system)]
    (b/star star camera)))

(defn draw-subsystems [system camera]
  (doseq [planet (get-planets system)]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size (* (planet :radius) (camera :obj-zoom))]
      (geo/half-circle pos size (get-in planet [:cylpos 1]) (planet :color))))
  (doseq [star (get-bodies system)]
    (b/star star camera)))

(defn draw-planet [refbody camera]
  (doseq [moon (refbody :moons)]
    (let [pos (t/map-to-screen (moon :mappos) camera)
          size (* (moon :radius) (camera :obj-zoom))]
      (b/planet pos size (get-in refbody [:cylpos 1]) (moon :color))))
  (let [pos (t/map-to-screen (refbody :mappos) camera)
        size (* (refbody :radius) (camera :obj-zoom))]
    (b/planet pos size (get-in refbody [:cylpos 1]) (refbody :color))))

(defn draw-refbody [system camera]
  (let [refbody (cam/get-refbody camera system)]
    (case (refbody :type)
      :planet (draw-planet refbody camera)
      :star (b/star refbody camera))))