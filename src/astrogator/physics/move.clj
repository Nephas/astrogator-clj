(ns astrogator.physics.move
  (:require [astrogator.physics.trafo :as t]
            [astrogator.conf :as c]))

;===== SYSTEM MOVEMENT =====
(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move
  ([system dt cylpos mappos]
   (if (some? (get system :system))
     (let [subsystem (system :system)
           phase (subsystem :phase)
           phiA (+ phase (* dt (subsystem :cylvel)))
           phiB (+ phiA Math/PI)
           cylposA [(subsystem :radiusA) phiA]
           cylposB [(subsystem :radiusB) phiB]]
       (-> system
           (update-in [:compA] move dt cylposA (cyl-to-map mappos cylposA))
           (update-in [:compB] move dt cylposB (cyl-to-map mappos cylposB))
           (assoc-in [:system :phase] phiA)
           (assoc-in [:system :mappos] mappos)))
     (-> system
         (assoc-in [:star :cylpos] cylpos)
         (assoc-in [:star :mappos] mappos))))
  ([system dt] (move system dt [0 0] [0 0])))

(defn move-viewsystem [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (update-in state [:universe :viewsystem] move dpf)))