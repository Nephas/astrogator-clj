(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]))

(defprotocol Transit
  (transit-move [this dt origin-mappos target-mappos]))

(defrecord Trajectory [par time])

(defn move-towards-target [ship dt system]
  (let [par (get-in ship [:transit :par])
        origin-path (:transit-origin ship)
        target-path (:transit-target ship)]
    (if (< par 1)
      (transit-move ship dt
                    (get-in system (conj origin-path :mappos))
                    (get-in system (conj target-path :mappos)))
      (o/place-in-orbit ship target-path (get-in system target-path)))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos]
  (let [transit (:transit ship)
        new-par (+ (/ dt (:time transit)) (:par transit))
        mappos-progress (t/scalar new-par (t/sub target-mappos origin-mappos))]
    (-> ship
        (assoc-in [:transit :par] new-par)
        (assoc-in [:mappos] (t/add origin-mappos mappos-progress)))))

(defn start-transit [ship target-path]
  (let [origin-path (:orbit-parent ship)]
    (if (not= target-path origin-path)
      (-> ship
          (assoc-in [:orbit-parent] nil)
          (assoc-in [:orbit] nil)
          (assoc-in [:ai-mode] :transit)
          (assoc-in [:transit-origin] origin-path)
          (assoc-in [:transit-target] target-path)
          (assoc-in [:transit] (->Trajectory 0 30)))
      ship)))