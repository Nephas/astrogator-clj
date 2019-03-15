(ns astrogator.physics.move.system
  (:require [astrogator.conf :as c]
            [astrogator.physics.move.move :as m]
            [astrogator.physics.move.orbit :as o]))

(defn move-system
  ([system dt cylpos mappos]
   (if (some? (system :system))
     (let [{phase   :phase
            cylvel  :cylvel
            radiusA :radiusA
            radiusB :radiusB} (:system system)
           phiA (+ phase (* dt cylvel))
           phiB (+ phiA Math/PI)
           cylposA [radiusA phiA]
           cylposB [radiusB phiB]]
       (-> system
           (update-in [:compA] move-system dt cylposA (o/cyl-to-map mappos cylposA))
           (update-in [:compB] move-system dt cylposB (o/cyl-to-map mappos cylposB))
           (update-in [:planets] m/move-planets dt mappos)
           (update-in [:asteroids] m/move-particles dt mappos)
           (assoc-in [:system :phase] phiA)
           (assoc-in [:system :mappos] mappos)))
     (-> system
         (update-in [:planets] m/move-planets dt mappos)
         (update-in [:asteroids] m/move-particles dt mappos)
         (assoc-in [:body :cylpos] cylpos)
         (assoc-in [:body :mappos] mappos))))
  ([system dt] (let [moved-system (move-system system dt [0 0] [0 0])]
                 (update-in moved-system [:ships] m/move-ships dt moved-system))))

(defn move-refsystem [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (-> state
        (update-in [:time :day] #(float (+ dpf %)))
        (update-in [:universe :refsystem] move-system dpf))))