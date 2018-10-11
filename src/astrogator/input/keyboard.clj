(ns astrogator.input.keyboard
  (:require [astrogator.render.render :as r]))

(defn handle-key [state event]
      (case (:key event)
        (:+) (r/zoom :in state)
        (:-) (r/zoom :out state)
        (:1) (update-in state [:time :dps] #(* 1/2 %))
        (:2) (update-in state [:time :dps] #(* 2 %))
        ;(:r) (state/init-state)
        (:w :up) state
        (:s :down) state
        (:a :left) state
        (:d :right) state
        state))