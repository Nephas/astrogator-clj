(ns astrogator.physics.move.ship
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.gravity :as g]
            [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.transit :as tr]
            [astrogator.physics.units :as u]
            [astrogator.util.log :as log]
            [astrogator.physics.move.clock :as c]))

(defn shipacc [ship]
  (let [thrust (* (:throttle ship) (:thrust ship))]
    (t/scalar thrust (t/pol-to-cart 1 (:pointing ship)))))

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

(defn consume-deltav [ship dt] "in AU/d"
  (let [deltav (* (t/norm (:mapacc ship)) dt)]
    (cond (= :interplanetary (:ai-mode ship)) deltav
          (= :interstellar (:ai-mode ship)) (* 0.01 deltav)
          true 0)))

(defn move-ship [ship dt system]
  (let [moved-ship (cond
                     (nil? (:ai-mode ship)) (move-in-potential ship dt system)
                     (some? (:orbit ship)) (o/orbit-move ship dt (get-in system (conj (get-in ship [:orbit :parent]) :mappos)))
                     (some? (:transit ship)) (tr/transit-by-scope ship dt system)
                     true ship)
        mapvel (t/scalar (/ 1 dt) (t/sub (:mappos moved-ship) (:mappos ship)))
        mapacc (t/scalar (/ 1 dt) (t/sub mapvel (:mapvel ship)))
        consumed (u/conv (consume-deltav moved-ship dt) :AU/d :km/s)
        out-of-fuel? (neg? (:deltav ship))
        beta (min 1 (u/conv (t/norm mapvel) :AU/d :c))]
    (-> moved-ship
        (assoc :mapvel mapvel)
        (assoc :mapacc mapacc)
        (assoc :beta beta)
        (update :deltav - consumed)
        (update :ai-mode (if out-of-fuel? (fn [_] nil) identity))
        (update :time c/tick dt beta))))