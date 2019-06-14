(ns astrogator.render.conf)

(def font-size 14)

(def back-color [0.7 0.2 0.2])

(def particle-color [0.6 0.5 0.7])

(def planet-shade-color [0.7 0.1 0.1])
(def planet-night-color [0.7 0.1 0.1])

(def moon-surface-color [0 0 0.5])

(def gui-back [0.1 0.2 0.2])
(def gui-secondary [0.1 0.5 1])
(def gui-primary [0.0 0.5 1])

(def margin {:left 50
             :right 50
             :top 50
             :bottom 50})

(def airy-threshold 3)

(def thresholds
  {:body      10000
   :subsystem 10
   :system    0.1
   :sector    0})