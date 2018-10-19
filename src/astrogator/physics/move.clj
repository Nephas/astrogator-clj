(ns astrogator.physics.move
  (:require [astrogator.physics.trafo :as t]
            [astrogator.conf :as c]))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-planets [planets dt parent-mappos]
  (let [move (fn [planet]
               (let [phase (get-in planet [:cylpos 1])
                     radius (get-in planet [:cylpos 0])
                     new-phase (+ phase (* dt (planet :cylvel)))
                     new-cylpos [radius new-phase]
                     mappos (cyl-to-map parent-mappos new-cylpos)]
                 (-> planet
                     (assoc-in [:mappos] mappos)
                     (assoc-in [:cylpos] new-cylpos))))]
    (map move planets)))

(defn move-system
  ([system dt cylpos mappos]
   (if (some? (system :system))
     (let [subsystem (system :system)
           phase (subsystem :phase)
           phiA (+ phase (* dt (subsystem :cylvel)))
           phiB (+ phiA Math/PI)
           cylposA [(subsystem :radiusA) phiA]
           cylposB [(subsystem :radiusB) phiB]]
       (-> system
           (update-in [:compA] move-system dt cylposA (cyl-to-map mappos cylposA))
           (update-in [:compB] move-system dt cylposB (cyl-to-map mappos cylposB))
           (update-in [:planets] move-planets dt mappos)
           (assoc-in [:system :phase] phiA)
           (assoc-in [:system :mappos] mappos)))
     (-> system
         (update-in [:planets] move-planets dt mappos)
         (assoc-in [:star :cylpos] cylpos)
         (assoc-in [:star :mappos] mappos))))
  ([system dt] (move-system system dt [0 0] [0 0])))

(defn move-viewsystem [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (update-in state [:universe :viewsystem] move-system dpf)))