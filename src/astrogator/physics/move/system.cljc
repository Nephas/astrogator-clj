(ns astrogator.physics.move.system
  (:require [astrogator.conf :as c]
            [astrogator.physics.move.move :as m]
            [astrogator.physics.move.orbit :as o]))

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
           (update-in [:compA] move-system dt cylposA (o/cyl-to-map mappos cylposA))
           (update-in [:compB] move-system dt cylposB (o/cyl-to-map mappos cylposB))
           (update-in [:planets] m/move-planets dt mappos)
           (update-in [:ships] m/move-ships dt system)
           (update-in [:asteroids] m/move-particles dt mappos)
           (assoc-in [:system :phase] phiA)
           (assoc-in [:system :mappos] mappos)))
     (-> system
         (update-in [:planets] m/move-planets dt mappos)
         (update-in [:ships] m/move-ships dt system)
         (update-in [:asteroids] m/move-particles dt mappos)
         (assoc-in [:body :cylpos] cylpos)
         (assoc-in [:body :mappos] mappos))))
  ([system dt] (move-system system dt [0 0] [0 0])))

(defn move-viewsystem [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (-> state
        (update-in [:time :day] #(float (+ dpf %)))
        (update-in [:universe :viewsystem] move-system dpf))))