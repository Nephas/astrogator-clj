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
            [astrogator.util.rand :as r]
            [astrogator.util.util :as util]
            [astrogator.poetry.names :as n]
            [astrogator.physics.move.orbit :as orb]))

(def max-dv (u/conv 100000 :m/s2 :AU/d2))

(defrecord Ship [name mappos mapvel mapacc dv max-dv thrust ai-mode time])

(extend Ship trafo/Distance trafo/distance-impl)
(extend Ship draw/Drawable draw/drawable-impl)
(extend Ship trail/Trail trail/trail-impl)
(extend Ship orb/Orbit orb/orbit-impl)

(defn ship [name]
  (->Ship name [0 0] [0 0] [0 0] max-dv max-dv 0 :orbit (c/clock)))

(defn init-playership [state]
  (let [system (sel/get-expanded-refsystem state)
        parent (gs/get-random-planet system)
        orbit-radius (* (r/uniform 0.2 0.8) (:rhill parent))
        ship (-> (ship "You")
                 (o/init-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent)))]
    (-> state
        (cam/change-refbody (gs/get-closest-planet-or-star system (:mappos parent)))
        (update-in sel/ships-path #(cons ship %)))))

(defn init-npcship [system]
  (let [parent (gs/get-random-planet system)
        name (str (n/generate-name 2) "-" (r/rand-n 100))
        orbit-radius (* 10 (u/conv (:radius parent) :Re :AU))
        npc-ship (-> (ship name)
                     (o/init-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent)))]
    (-> system (update :ships #(cons npc-ship %)))))

(defn init-npcs [system]
  ((util/times (r/rand-n 10 50) init-npcship) system))
