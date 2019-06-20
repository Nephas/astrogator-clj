(ns astrogator.generation.player
  (:require [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as t]
            [astrogator.gui.selectors :as gs]
            [astrogator.physics.move.clock :as c]
            [astrogator.state.selectors :as sel]
            [astrogator.gui.camera :as cam]
            [astrogator.physics.trafo :as trafo]
            [astrogator.physics.units :as u]
            [astrogator.render.body.body :as draw]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]))

(defrecord Pilot [age sanity])

(defrecord Ship [orbit mappos mapvel mapacc deltav thrust pointing ai-mode time]
  o/Orbit
  (orbit-move [this dt parent-mappos] (o/move-around-parent this dt parent-mappos))

  trafo/Distance
  (dist [this other] (trafo/v-dist (:mappos this) (:mappos other)))

  draw/Drawable
  (draw-distant [this camera]
    (draw/particle (trafo/map-to-screen (:mappos this) camera) conf/particle-color))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (draw/draw-detail this camera)))

(defn ship [orbit]
  (->Ship orbit [0 0] [0 0] [0 0] 1000 0 0 :orbit (c/clock)))

(defn init-playership [state]
  (let [mappos [1 1]
        system (sel/get-expanded-refsystem state)
        parent (gs/get-closest-planet system mappos)
        orbit-radius (* 10 (u/conv (:radius parent) :Re :AU))
        orbit (o/circular-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent))]
    (-> state
        (cam/change-refbody (gs/get-closest-planet-or-star system mappos))
        (assoc-in sel/playership-path (ship orbit)))))

(defn init-npcship [state]
  (let [mappos [1 1]
        system (sel/get-expanded-refsystem state)
        parent (gs/get-closest-planet system mappos)
        orbit-radius (* 10 (u/conv (:radius parent) :Re :AU))
        orbit (o/circular-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent))]
    (-> state
        (cam/change-refbody (gs/get-closest-planet-or-star system mappos))
        (update-in sel/ships-path #(conj % (ship orbit))))))