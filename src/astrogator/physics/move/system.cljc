(ns astrogator.physics.move.system
  (:require [astrogator.conf :as conf]
            [astrogator.physics.move.orbit :as o]
            [astrogator.physics.move.clock :as c]
            [astrogator.util.util :as u]
            [astrogator.util.selectors :as sel]
            [astrogator.physics.move.ship :as s]
            [astrogator.physics.move.rotate :as rot]
            [astrogator.gui.sector :as sec]
            [astrogator.util.log :as log]))

(defn move-planet [planet dt parent-mappos]
  (let [moved-planet (-> planet
                         (rot/rotate dt)
                         (o/orbit-move dt parent-mappos))]
    (u/update-all moved-planet :moons o/orbit-move dt (:mappos moved-planet))))

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
         (update-in [:body] rot/rotate dt)
         (move-children dt mappos))))
  ([system dt] (let [moved-system (move-system system dt [0 0] [0 0])]
                 (u/update-all moved-system :ships s/move-ship dt moved-system))))

(defn swap-refsystem [state]
  (let [ship (sel/get-playership state)
        targetseed (:swapsystem ship)
        originseed (:seed (sel/get-refsystem state))]
    (if (or (nil? targetseed)
            (false? targetseed)) state
                                 (do (log/info "transitioning from system " originseed " to " targetseed)
                                     (-> state
                                         (assoc-in (conj sel/playership-path :swapsystem) false)
                                         (sec/change-refsystem (sel/get-system-by-seed state targetseed)))))))

(defn move-universe [state]
  (let [dt (float (/ (get-in state [:time :dps]) conf/frame-rate))]
    (-> state
        (update-in [:time] c/tick dt)
        (update-in [:universe :refsystem] move-system dt))))