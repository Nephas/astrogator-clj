(ns astrogator.input.keyboard
  (:require [astrogator.gui.camera :as c]
            [astrogator.util.selectors :as s]
            [astrogator.physics.move.orbit :as o]))

(defn handle-key [state event]
      (case (:key event)
        (:+) (c/zoom :in state)
        (:-) (c/zoom :out state)
        (:<) (update-in state [:time :dps] #(* 0.5 %))
        (:>) (update-in state [:time :dps] #(* 2 %))
        (:1) (assoc-in state [:camera :map-mode] :physical)
        (:2) (assoc-in state [:camera :map-mode] :heat)
        (:3) (assoc-in state [:camera :map-mode] :height)
        (:r) (assoc-in state [:universe :reset] true)
        (:w :up) (update-in state (conj s/playership-path :throttle) #(min 1 (+ % 0.1)))
        (:s :down) (update-in state (conj s/playership-path :throttle) #(max 0 (- % 0.1)))
        (:m) (assoc-in state (conj s/playership-path :mapvel) ((s/get-target state) :mapvel))
        (:a :left) state
        (:d :right) state
        (:o) (update-in state s/playership-path
                        #(o/toggle-orbit % (state :camera) (get-in state [:universe :viewsystem])))
        state))

(defn reset? [state]
  (get-in state [:universe :reset]))