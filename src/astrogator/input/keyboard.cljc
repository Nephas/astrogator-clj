(ns astrogator.input.keyboard
  (:require [astrogator.gui.camera :as c]
            [astrogator.util.selectors :as s]
            [astrogator.util.log :as log]
            [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as t]))

(defn handle-key [state event]
  (do (log/debug "keypress: " (:key event))
      (case (:key event)
        (:up) (c/zoom :in state)
        (:down) (c/zoom :out state)
        (:left) (update-in state [:time :dps] #(* 0.5 %))
        (:right) (update-in state [:time :dps] #(* 2 %))
        (:1) (assoc-in state [:camera :map-mode] :physical)
        (:2) (assoc-in state [:camera :map-mode] :heat)
        (:3) (assoc-in state [:camera :map-mode] :height)
        (:r) (assoc-in state [:universe :reset] true)
        (:w) (update-in state (conj s/playership-path :throttle) #(min 1 (+ % 0.1)))
        (:s) (update-in state (conj s/playership-path :throttle) #(max 0 (- % 0.1)))
        (:m) (assoc-in state (conj s/playership-path :mapvel) ((s/get-targetbody state) :mapvel))
        (:a) state
        (:d) state
        (:t) (update-in state s/playership-path #(t/start-transit % (state :camera) (s/get-expanded-refsystem state)))
        (:o) (update-in state s/playership-path #(o/toggle-orbit % (state :camera) (s/get-expanded-refsystem state)))
        state)))

(defn reset? [state]
  (get-in state [:universe :reset]))