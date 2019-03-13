(ns astrogator.physics.move.move
  (:require [astrogator.physics.move.ship :as s]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.util :as u]))

(defn move-moons [planet dt]
  (let [move-around-parent #(o/orbit-move % dt (:mappos planet))]
    (u/update-list planet :moons move-around-parent)))

(defn move-planets [planets dt parent-mappos]
  (let [move-lunar-system #(-> % (o/orbit-move dt parent-mappos)
                               (move-moons dt))]
    (mapv move-lunar-system planets)))

(defn move-particles [particles dt parent-mappos]
  (mapv #(o/orbit-move % dt parent-mappos) particles))

(defn move-ships [ships dt system]
  (mapv #(s/move-ship % dt system) ships))