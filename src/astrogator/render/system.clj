(ns astrogator.render.system
  (:require [astrogator.render.geometry :as geo]
            [astrogator.render.body :as b]
            [astrogator.render.field :as f]
            [astrogator.physics.trafo :as t]
            [astrogator.gui.camera :as cam]
            [quil.core :as q]
            [astrogator.util.color :as col]))

(defn get-all [system key]
  (if (nil? (system :body))
    (concat (get-all (system :compA) key) (get-all (system :compB) key) (system key))
    (system key)))

(defn get-bodies [system]
  (if (nil? (system :body))
    (concat (get-bodies (system :compA)) (get-bodies (system :compB)))
    [(system :body)]))

(defn draw-asteroids [particles camera]
  (q/no-stroke)
  (col/fill [128 128 128])
  (doseq [particle particles]
    (let [pos (t/map-to-screen (particle :mappos) camera)]
      (geo/circle pos 1))))

(defn draw-planets [planets camera]
  (doseq [planet planets]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size (* 0.1 (planet :radius) (camera :obj-zoom))]
      (b/distant-planet pos size (get-in planet [:cylpos 1]) (planet :color)))))

(defn draw-stars [stars camera]
  (doseq [star stars]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* 5 (camera :obj-zoom) (star :radius))]
      (b/distant-star pos size (star :color)))))

(defn draw-system [system camera]
  (f/draw-field system camera)
  (draw-asteroids (get-all system :particles) camera)
  (draw-planets (get-all system :planets) camera)
  (draw-stars (get-bodies system) camera))

(defn draw-refbody [system camera]
  (let [refbody (cam/get-refbody camera system)]
    (case (refbody :type)
      :planet (b/draw-planet refbody camera)
      :star (b/draw-star refbody camera))))