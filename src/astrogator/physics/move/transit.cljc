(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.log :as log]
            [astrogator.physics.units :as u]))

(defprotocol Transit
  (transit-move [this dt origin-mappos target-mappos]))

(defrecord Trajectory [par parvel paracc parlength origin target])

(defn brachistochrone [g-acc length origin target]
  (->Trajectory 0 0 (u/conv g-acc :g :AU/d2) length origin target))

(defn move-towards-target [ship dt system]
  (let [{par         :par
         origin-path :origin
         target-path :target
         parlength   :parlength} (:transit ship)
        origin (get-in system origin-path)
        target (get-in system target-path)]
    (if (< par parlength)
      (transit-move ship dt (:mappos origin) (:mappos target))
      (o/place-in-orbit ship target-path (get-in system target-path)))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos]
  (let [{par       :par
         parvel    :parvel
         paracc    :paracc
         parlength :parlength} (:transit ship)
        new-parvel (if (< par (* 0.5 parlength))
                     (+ parvel (* dt paracc))
                     (- parvel (* dt paracc)))
        new-par (+ (* parvel dt) par)
        progress (/ new-par parlength)
        mappos-progress (t/scalar progress (t/sub target-mappos origin-mappos))]
    (-> ship
        (assoc-in [:transit :par] new-par)
        (assoc-in [:transit :parvel] new-parvel)
        (assoc-in [:mappos] (t/add origin-mappos mappos-progress)))))

(defn start-transit [ship camera system]
  (let [target-path (:targetbody camera)
        origin-path (get-in ship [:orbit :parent])
        dist (t/dist (get-in system (conj origin-path :mappos))
                     (get-in system (conj target-path :mappos)))]
    (if (not= target-path origin-path)
      (do (log/info "ship on transit trajectory to: " target-path)
          (-> ship
              (assoc-in [:orbit] nil)
              (assoc-in [:ai-mode] :transit)
              (assoc-in [:transit] (brachistochrone 0.01 dist origin-path target-path))))
      ship)))