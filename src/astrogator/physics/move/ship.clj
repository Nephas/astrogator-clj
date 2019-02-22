(ns astrogator.physics.move.ship
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.gravity :as g]
            [astrogator.physics.move.orbit :as o]))

(defn shipacc [ship]
  (let [thrust (* (ship :throttle) (ship :thrust))]
    (t/scalar thrust (t/pol-to-cart 1 (ship :pointing)))))

(defn acc-at-pos [mappos system]
  (let [gravacc (g/gravacc-at-pos mappos system)
        shipacc (shipacc (get-in system [:ships 0]))]
    (t/add gravacc shipacc)))

(defn move-in-potential [body dt system]
  (let [mapacc (acc-at-pos (:mappos body) system)
        intervel (t/add (t/scalar (* 1/2 dt) mapacc) (:mapvel body))
        mappos (t/add (t/scalar dt intervel) (:mappos body))
        interacc (acc-at-pos mappos system)
        mapvel (t/add (t/scalar dt interacc) intervel)]
    (-> body
        (assoc-in [:mapacc] mapacc)
        (assoc-in [:mapvel] mapvel)
        (assoc-in [:mappos] mappos))))

(defn move-ship [ship dt system]
  (case (ship :ai-mode)
    nil (move-in-potential ship dt system)
    :orbit (o/move-around-parent ship dt (get-in system (conj (ship :orbit-parent) :mappos)))))