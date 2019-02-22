(ns astrogator.physics.move.move
  (:require [astrogator.physics.move.ship :as s]
            [astrogator.physics.move.orbit :as o]))

;TODO extract move protocol
(defn move-moons [planet dt]
  (let [move (fn [moons] (mapv #(o/move-around-parent % dt (:mappos planet)) moons))]
    (update-in planet [:moons] move)))

(defn move-planets [planets dt parent-mappos]
  (let [move-planet-moon-system (fn [planet]
                                  (-> planet
                                      (o/move-around-parent dt parent-mappos)
                                      (move-moons dt)))]
    (mapv move-planet-moon-system planets)))

(defn move-particles [particles dt parent-mappos]
  (mapv #(o/move-around-parent % dt parent-mappos) particles))

(defn move-ships [ships dt system]
  (mapv #(s/move-ship % dt system) ships))