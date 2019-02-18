(ns astrogator.render.system
  (:require [astrogator.render.geometry :as geo]
            [astrogator.render.body :as b]
            [astrogator.render.field :as f]
            [astrogator.render.ship :as sh]
            [astrogator.physics.trafo :as t]
            [astrogator.util.selectors :as s]
            [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.util.log :as log]))

(defn draw-asteroids [particles camera]
  (q/no-stroke)
  (col/fill [128 128 128])
  (doseq [particle particles]
    (let [pos (t/map-to-screen (particle :mappos) camera)]
      (geo/circle pos 1))))

(defn draw-ships [ships camera]
  (col/fill [128 128 128])
  (doseq [ship ships]
    (let [pos (t/map-to-screen (ship :mappos) camera)]
      (sh/render-ship ship pos))))

(defn draw-planets [planets camera]
  (doseq [planet planets]
    (let [pos (t/map-to-screen (planet :mappos) camera)
          size (* 0.1 (planet :radius) (camera :obj-zoom))]
      (do (f/draw-soi planet camera (planet :color))
          (b/distant-planet pos size (get-in planet [:cylpos 1]) (planet :color))))))

(defn draw-stars [stars camera]
  (doseq [star stars]
    (let [pos (t/map-to-screen (star :mappos) camera)
          size (* 5 (camera :obj-zoom) (star :radius))]
      (do (f/draw-soi star camera (star :color))
          (b/distant-star pos size (star :color))))))

(defn draw-systems [systems camera]
  (doseq [system systems]
    (f/draw-soi system camera [96 96 96])))

(defn draw-system [system camera]
  ;(f/draw-gravity-field system camera)
  (draw-systems (s/get-subsystems system) camera)
  (draw-asteroids (s/get-all system :particles) camera)
  (draw-planets (s/get-all system :planets) camera)
  (draw-ships (s/get-all system :ships) camera)
  (draw-stars (s/get-bodies system) camera))

(defn draw-refbody [system camera]
  (let [refbody (s/get-refbody camera system)]
    (case (refbody :type)
      :planet (b/draw-planet refbody camera)
      :star (b/draw-star refbody camera))))