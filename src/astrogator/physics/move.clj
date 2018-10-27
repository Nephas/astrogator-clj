(ns astrogator.physics.move
  (:require [astrogator.physics.trafo :as t]
            [astrogator.conf :as c]))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-around-parent [body dt parent-mappos]
  (let [phase (get-in body [:cylpos 1])
        radius (get-in body [:cylpos 0])
        new-phase (+ phase (* dt (body :cylvel)))
        new-cylpos [radius new-phase]
        mappos (cyl-to-map parent-mappos new-cylpos)]
    (-> body
        (assoc-in [:mappos] mappos)
        (assoc-in [:cylpos] new-cylpos))))

(defn move-moons [planet dt]
  (let [move (fn [moons] (mapv #(move-around-parent % dt (planet :mappos)) moons))]
    (update-in planet [:moons] move)))

(defn move-planets [planets dt parent-mappos]
  (let [move-planet-moon-system (fn [planet]
                                  (-> planet
                                      (move-around-parent dt parent-mappos)
                                      (move-moons dt)))]
    (mapv move-planet-moon-system planets)))

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
         (assoc-in [:body :cylpos] cylpos)
         (assoc-in [:body :mappos] mappos))))
  ([system dt] (move-system system dt [0 0] [0 0])))

(defn move-viewsystem [state]
  (let [dpf (/ (get-in state [:time :dps]) c/frame-rate)]
    (update-in state [:universe :viewsystem] move-system dpf)))