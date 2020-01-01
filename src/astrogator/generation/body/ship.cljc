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
            [astrogator.render.draw.geometry :as geo]))

(def max-dv (u/conv 100000 :m/s2 :AU/d2))

(defrecord Ship [orbit mappos mapvel mapacc dv max-dv thrust pointing ai-mode time]
  o/Orbit
  (orbit-move [this dt parent-mappos] (o/move-around-parent this dt parent-mappos))

  trafo/Distance
  (dist [this other] (trafo/v-dist (:mappos this) (:mappos other)))

  trail/Trail
  (extend [this t] (trail/update-trail this t))

  draw/Drawable
  (draw-distant [this camera]
    (geo/particle (trafo/map-to-screen (:mappos this) camera) conf/particle-color))
  (draw-surface [this camera] nil)
  (draw-detail [this camera]
    (draw/draw-distant this camera))
  (draw-trail [this camera]
    (trail/draw-trail this camera conf/particle-color)))

(defn ship [orbit]
  (->Ship orbit [0 0] [0 0] [0 0] max-dv max-dv 0 0 :orbit (c/clock)))

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