(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.log :as log]))

(defprotocol Transit
  (transit-move [this dt origin-mappos target-mappos]))

(defrecord Trajectory [par parvel time origin target])

(defn move-towards-target [ship dt system]
  (let [par (get-in ship [:transit :par])
        origin-path (get-in ship [:transit :origin])
        target-path (get-in ship [:transit :target])]
    (if (< par 1)
      (transit-move ship dt
                    (get-in system (conj origin-path :mappos))
                    (get-in system (conj target-path :mappos)))
      (o/place-in-orbit ship target-path (get-in system target-path)))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos]
  (let [transit (:transit ship)
        ;new-parvel (+ (:parvel ship) (* dt 0.01))
        ;(if (< (:par transit) 0.5)
        ;(- (:parvel ship) (* dt paracc)))
        new-par (+ (/ dt (:time transit)) (:par transit))
        mappos-progress (t/scalar new-par (t/sub target-mappos origin-mappos))]
    (-> ship
        (assoc-in [:transit :par] new-par)
        (assoc-in [:mapvel] [0 0])
        (assoc-in [:mappos] (t/add origin-mappos mappos-progress)))))

(defn start-transit [ship camera system]
  (let [target-path (:targetbody camera)
        origin-path (get-in ship [:orbit :parent])
        dist (t/dist (get-in system (conj origin-path :mappos))
                     (get-in system (conj target-path :mappos)))]
    (if (not= target-path origin-path)
      (do (log/info (str "ship on transit trajectory to: " target-path))
          (-> ship
              (assoc-in [:orbit] nil)
              (assoc-in [:ai-mode] :transit)
              (assoc-in [:transit] (->Trajectory 0 0 (* 10 dist) origin-path target-path))))
      ship)))