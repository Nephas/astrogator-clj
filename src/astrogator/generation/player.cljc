(ns astrogator.generation.player
  (:require [astrogator.physics.move.orbit :as o]
            [astrogator.gui.system :as sys]
            [astrogator.util.log :as log]
            [astrogator.util.selectors :as s]))

(defrecord Ship [orbit mappos mapvel throttle thrust pointing orbit-parent ai-mode]
  o/Orbit (orbit [this dt parent-mappos] (o/move-around-parent this dt parent-mappos)))

(defn generate-playership [parent-path orbit]
  (->Ship orbit [0 0] [0 0] 0 0 0 parent-path :orbit))

(defn place-playership [system]
  (let [parent (sys/get-closest-planet system [1 1])
        orbit-radius (* 0.5 (:rhill parent))
        orbit (o/circular-orbit (:mass parent) :Me [orbit-radius nil])]
    (assoc system :ships [(generate-playership (:path parent) orbit)])))
