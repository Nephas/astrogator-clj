(ns astrogator.generation.player
  (:require [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as t]
            [astrogator.gui.selectors :as gs]
            [astrogator.physics.move.clock :as c]
            [astrogator.state.selectors :as sel]
            [astrogator.gui.camera :as cam]))

(defrecord Ship [orbit mappos mapvel throttle thrust pointing ai-mode time]
  o/Orbit (orbit-move [this dt parent-mappos] (o/move-around-parent this dt parent-mappos))
  t/Transit (transit-move [this dt origin-mappos target-mappos] (t/move-on-trajectory this dt origin-mappos target-mappos)))

(defn generate-playership [orbit]
  (->Ship orbit [0 0] [0 0] 0 0 0 :orbit (c/clock)))

(defn init-playership [state]
  (let [mappos [1 1]
        system (sel/get-expanded-refsystem state)
        parent (gs/get-closest-planet system mappos)
        orbit-radius (* 0.5 (:rhill parent))
        orbit (o/circular-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent))]
    (-> state
        (cam/change-refbody (gs/get-closest-planet-or-star system mappos))
        (assoc-in sel/playership-path (generate-playership orbit)))))