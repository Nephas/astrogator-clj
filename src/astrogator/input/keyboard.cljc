(ns astrogator.input.keyboard
  (:require [astrogator.gui.camera :as c]
            [astrogator.util.selectors :as s]
            [astrogator.util.log :as log]
            [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as t]
            [quil.core :as q]))

(def coded-keys {32 :space
                 8  :back
                 9  :tab
                 10 :enter})

(defn handle-key [state event]
  (let [coded-key (coded-keys (q/key-code))
        key (if (some? coded-key) coded-key (:key event))]
    (do (log/debug "keypress: " (q/key-code) " - " (:key event))
        (case key
          (:up) (update state :camera #(c/zoom % :in))
          (:down) (update state :camera #(c/zoom % :out))
          (:left) (update-in state [:time :dps] #(* 0.5 %))
          (:right) (update-in state [:time :dps] #(* 2 %))
          (:space) (update-in state s/playership-path #(t/start-transit % (state :camera) (s/get-expanded-refsystem state) (s/get-sector state)))
          (:1) (assoc-in state [:camera :map-mode] :physical)
          (:2) (assoc-in state [:camera :map-mode] :heat)
          (:3) (assoc-in state [:camera :map-mode] :height)
          (:r) (assoc-in state [:universe :reset] true)
          (:w) (update-in state (conj s/playership-path :throttle) #(min 1 (+ % 0.1)))
          (:s) (update-in state (conj s/playership-path :throttle) #(max 0 (- % 0.1)))
          (:m) (assoc-in state (conj s/playership-path :mapvel) ((s/get-targetbody state) :mapvel))
          (:o) (update-in state s/playership-path #(o/toggle-orbit % (state :camera) (s/get-expanded-refsystem state)))
          state))))

(defn reset? [state]
  (get-in state [:universe :reset]))