(ns astrogator.physics.move.ship
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.gravity :as g]
            [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as tr]
            [astrogator.physics.units :as u]
            [astrogator.physics.move.clock :as c]
            [astrogator.physics.trail :as trail]))

(defn acc-at-pos [mappos system]
  (let [gravacc (g/gravacc-at-pos mappos system)
        shipacc [0 0]]
    (t/add gravacc shipacc)))

(defn move-in-potential [body dt system]
  (let [mapacc (acc-at-pos (:mappos body) system)
        intervel (t/add (t/scalar (* 0.5 dt) mapacc) (:mapvel body))
        mappos (t/add (t/scalar dt intervel) (:mappos body))
        interacc (acc-at-pos mappos system)
        mapvel (t/add (t/scalar dt interacc) intervel)]
    (-> body
        (assoc-in [:mapacc] mapacc)
        (assoc-in [:mapvel] mapvel)
        (assoc-in [:mappos] mappos))))

(defn consume-dv [ship dt] "in AU/d"
  (let [dv (* (t/norm (:mapacc ship)) dt)]
    (cond (= :interplanetary (:ai-mode ship)) dv
          (= :interstellar (:ai-mode ship)) (* 0.01 dv)
          true 0)))

(defn jumped-systems [moved-ship ship]
  (> (t/dist moved-ship ship) 10000))

(defn move-ship [ship dt system]
  (let [moved-ship (cond
                     (nil? (:ai-mode ship)) (move-in-potential ship dt system)
                     (some? (:orbit ship)) (o/orbit-move ship dt (get-in system (conj (get-in ship [:orbit :parent]) :mappos)))
                     (some? (:transit ship)) (tr/transit-by-scope ship dt system)
                     true ship)
        mapvel (t/scalar (/ 1 dt) (t/sub (:mappos moved-ship) (:mappos ship)))
        mapacc (t/scalar (/ 1 dt) (t/sub mapvel (:mapvel ship)))
        consumed (u/conv (consume-dv moved-ship dt) :AU/d :km/s)
        ;out-of-fuel? (neg? (:dv ship))
        out-of-fuel? false
        beta (min 1 (u/conv (t/norm mapvel) :AU/d :c))]
    (if (jumped-systems moved-ship ship)
      (-> moved-ship (update :time c/tick dt beta))
      (-> moved-ship
          (trail/update-step dt)
          (assoc :mapvel mapvel)
          (assoc :mapacc mapacc)
          (assoc :beta beta)
          (update :dv - consumed)
          (assoc :thrust (/ consumed dt))
          (update :ai-mode (if out-of-fuel? (fn [_] nil) identity))
          (update :time c/tick dt beta)))))