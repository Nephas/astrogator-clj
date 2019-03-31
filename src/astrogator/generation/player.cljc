(ns astrogator.generation.player
  (:require [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as t]
            [astrogator.gui.system :as sys]
            [astrogator.physics.move.clock :as c]))

(defrecord Ship [orbit mappos mapvel throttle thrust pointing ai-mode time]
  o/Orbit (orbit-move [this dt parent-mappos] (o/move-around-parent this dt parent-mappos))
  t/Transit (transit-move [this dt origin-mappos target-mappos] (t/move-on-trajectory this dt origin-mappos target-mappos)))

(defn generate-playership [orbit]
  (->Ship orbit [0 0] [0 0] 0 0 0 :orbit (c/clock)))

(defn place-playership
  ([system ship]
   (let [parent (sys/get-closest-planet system [1 1])
         orbit-radius (* 0.5 (:rhill parent))
         orbit (o/circular-orbit [(:mass parent) :Me] [orbit-radius nil] (:path parent))]
     (assoc system :ships [(if (nil? ship) (generate-playership orbit) ship)])))
  ([system] (place-playership system nil)))
