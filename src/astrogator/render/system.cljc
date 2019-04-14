(ns astrogator.render.system
  (:require [astrogator.render.geometry :as geo]
            [astrogator.render.body.body :as b]
            [astrogator.render.field :as f]
            [astrogator.physics.trafo :as t]
            [astrogator.state.selectors :as s]
            [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.render.conf :as conf]
            [astrogator.render.body.star :as st]
            [astrogator.render.body.planet :as p]))

;TODO extract draw protocol
(defn draw-asteroids [particles camera]
  (q/no-stroke)
  (col/fill conf/particle-color)
  (doseq [particle particles]
    (let [pos (t/map-to-screen (:mappos particle) camera)]
      (geo/circle pos 1))))

(defn draw-ships [ships camera]
  (col/fill [128 128 128])
  (doseq [ship ships]
    (let [pos (t/map-to-screen (:mappos ship) camera)]
      (geo/circle pos 1))))

(defn get-distant-color [planet]
  (let [{rock    :rock
         glacier :glacier
         ocean   :ocean} (:color planet)]
    (col/blend-vec-color rock ocean glacier)))

(defn draw-planets [planets camera]
  (doseq [planet planets]
    (let [pos (t/map-to-screen (:mappos planet) camera)
          size (* 0.1 (:radius planet) (camera :obj-zoom))
          phase (get-in planet [:cylpos 1])
          color (get-distant-color planet)]
      (do (f/draw-soi planet camera color)
          (b/distant-planet pos size phase color)))))

(defn draw-stars [stars camera]
  (doseq [star stars]
    (let [pos (t/map-to-screen (:mappos star) camera)
          size (* 5 (camera :obj-zoom) (:radius star))]
      (b/distant-star pos size (:color star)))))

(defn draw-system [system camera]
  (draw-asteroids (s/get-all system :asteroids) camera)
  (draw-planets (s/get-all system :planets) camera)
  (draw-ships (s/get-all system :ships) camera)
  (draw-stars (s/get-bodies system) camera))

(defn draw-refbody [system camera]
  (let [refbody (s/get-refbody camera system)]
    (draw-ships (s/get-all system :ships) camera)
    (cond
      (s/planet? refbody) (p/draw-planet refbody camera)
      (s/star? refbody) (st/draw-star refbody camera)
      true nil)))