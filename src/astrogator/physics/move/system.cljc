(ns astrogator.physics.move.system
  (:require [astrogator.conf :as c]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.util :as u]
            [astrogator.physics.move.ship :as s]
            [astrogator.physics.move.rotate :as rot]))

(defn move-planet [planet dt parent-mappos]
  (-> planet
      (rot/rotate dt)
      (o/orbit-move dt parent-mappos)
      (u/update-all :moons o/orbit-move dt (:mappos planet))))

(defn move-children [system dt parent-mappos]
  (-> system
      (u/update-all :planets move-planet dt parent-mappos)
      (u/update-all :asteroids o/orbit-move dt parent-mappos)))

(defn move-system
  ([system dt cylpos mappos]
   (if (some? (system :system))
     (let [{phase   :phase
            cylvel  :cylvel
            radiusA :radiusA
            radiusB :radiusB} (:system system)
           phase (+ phase (* dt cylvel))
           cylposA [radiusA phase]
           cylposB [radiusB (+ phase Math/PI)]]
       (-> system
           (update-in [:compA] move-system dt cylposA (o/cyl-to-map mappos cylposA))
           (update-in [:compB] move-system dt cylposB (o/cyl-to-map mappos cylposB))
           (assoc-in [:system :phase] phase)
           (assoc-in [:system :mappos] mappos)
           (move-children dt mappos)))
     (-> system
         (assoc-in [:body :cylpos] cylpos)
         (assoc-in [:body :mappos] mappos)
         (move-children dt mappos))))
  ([system dt] (let [moved-system (move-system system dt [0 0] [0 0])]
                 (u/update-all moved-system :ships s/move-ship dt moved-system))))

(defn move-time [time dt]
  (let [day (float (+ (:day time) dt))
        year (int (/ day 365.25))]
    (-> time
        (assoc :day day)
        (assoc :year year))))

(defn move-universe [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (-> state
        (update-in [:time] move-time dpf)
        (update-in [:universe :refsystem] move-system dpf))))