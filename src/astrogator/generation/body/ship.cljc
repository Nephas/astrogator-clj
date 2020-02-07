(ns astrogator.generation.body.ship
  (:require [astrogator.physics.move.orbit :as o]
            [astrogator.gui.selectors :as gs]
            [astrogator.physics.move.clock :as c]
            [astrogator.state.selectors :as sel]
            [astrogator.gui.camera :as cam]
            [astrogator.physics.trafo :as trafo]
            [astrogator.physics.trail :as trail]
            [astrogator.physics.units :as u]
            [astrogator.render.draw.body :as draw]
            [astrogator.render.conf :as conf]
            [astrogator.render.draw.geometry :as geo]
            [astrogator.util.rand :as r]
            [astrogator.util.util :as util]
            [astrogator.poetry.names :as n]
            [astrogator.util.color :as col]))

(def max-dv (u/conv 100000 :m/s2 :AU/d2))

(defrecord Ship [name orbit mappos mapvel mapacc dv max-dv thrust ai-mode time]
  o/Orbit
  (orbit-move [this dt parent-mappos] (o/move-around-parent this dt parent-mappos))

  trafo/Distance
  (dist [this other] (trafo/v-dist (:mappos this) (:mappos other)))

  trail/Trail
  (update-step [this t] (trail/update-trail this t))

  draw/Drawable
  (draw-distant [this camera]
    (geo/particle (trafo/map-to-screen (:mappos this) camera) conf/particle-color)
    (let [size (Math/log (max 1 (- (:thrust this) 10)))]
      (geo/circle (trafo/map-to-screen (:mappos this) camera) size (col/with-alpha conf/particle-color 64))))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (draw/draw-distant this camera))
  (draw-trail [this camera]
    (when (not (nil? (:transit this)))
      (trail/draw-trail this camera conf/particle-color))))

(defn ship [name orbit]
  (->Ship name orbit [0 0] [0 0] [0 0] max-dv max-dv 0 :orbit (c/clock)))

(defn init-playership [state]
  (let [system (sel/get-expanded-refsystem state)
        parent (gs/get-random-planet system)
        ship (o/place-in-orbit (ship "You" nil) system (:path parent))]
    (-> state
        (cam/change-refbody (gs/get-closest-planet-or-star system (:mappos parent)))
        (update-in sel/ships-path #(cons ship %)))))

(defn init-npcship [system]
  (let [parent (gs/get-random-planet system)
        name (str (n/generate-name 2) "-" (r/rand-n 100))
        orbit-radius (* 10 (u/conv (:radius parent) :Re :AU))
        orbit (o/circular-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent))]
    (-> system
        (update :ships #(cons (ship name orbit) %)))))

(defn init-npcs [system]
  ((util/times (r/rand-n 10 50) init-npcship) system))
