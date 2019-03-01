(ns astrogator.gui.animation)

(defn update-animations
  [state] (update-in state [:animation :target] inc))