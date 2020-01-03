(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.log :as log]
            [astrogator.physics.units :as u]
            [astrogator.state.selectors :as s]
            [astrogator.gui.selectors :as sel]
            [astrogator.physics.trafo :as trafo]
            [astrogator.util.math :as m]))

(declare move-on-trajectory)

(defrecord Trajectory [par parvel paracc parlength origin target offset scope])

(defn brachistochrone [g-acc length origin target offset scope]
  (->Trajectory 0 0 (u/conv g-acc :g :AU/d2) length origin target offset scope))

(defn smoothed-pos [system ship path]
  (let [body (get-in system path)
        frac (m/sigmoid (/ (trafo/dist ship body)
                           (get-in body [:orbit :cylpos 0])) 5)
        parentpos (:mappos (get-in system (sel/get-parent-path path)))
        bodypos (:mappos (get-in system path))]
    (trafo/midpoint frac bodypos parentpos)))

(defn move-interplanetary [ship dt system]
  (let [{par         :par
         origin-path :origin
         target-path :target
         offset      :offset
         parlength   :parlength} (:transit ship)
        originpos (smoothed-pos system ship origin-path)
        targetpos (smoothed-pos system ship target-path)]
    (if (< par parlength)
      (move-on-trajectory ship dt originpos targetpos offset)
      (o/place-in-orbit ship system target-path))))

(defn move-interstellar [ship dt system]
  (let [{par         :par
         origin-seed :origin
         target-seed :target
         offset      :offset
         parlength   :parlength} (:transit ship)
        target (s/get-system-by-seed target-seed)
        origin (s/get-system-by-seed origin-seed)
        targetpos (t/sub (:sectorpos target) (:sectorpos origin))]
    (cond (< par (* 0.5 parlength)) (move-on-trajectory ship dt [0 0] targetpos offset)
          (> par (* 1 parlength)) (let [targetplanet (sel/get-closest-planet system [0 0])]
                                    (o/place-in-orbit ship system (:path targetplanet)))
          true (-> ship
                   (move-on-trajectory dt (t/neg targetpos) [0 0] offset)
                   (assoc :swapsystem target-seed)))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos offset]
  (let [{par       :par
         parvel    :parvel
         paracc    :paracc
         parlength :parlength} (:transit ship)
        origin-mappos (t/add origin-mappos offset)
        new-parvel (cond (< par (* 0.4 parlength)) (+ parvel (* dt paracc))
                         (> par (* 0.6 parlength)) (- parvel (* dt paracc))
                         true parvel)
        new-par (+ (* parvel dt) par)
        progress (/ new-par parlength)
        mappos-progress (t/scalar progress (t/sub target-mappos origin-mappos))]
    (-> ship
        (assoc-in [:transit :par] new-par)
        (assoc-in [:transit :parvel] new-parvel)
        (assoc-in [:mappos] (t/add origin-mappos mappos-progress)))))

(defn transit-by-scope [ship dt system]
  (let [scope (get-in ship [:transit :scope])]
    (cond (= :interplanetary scope) (move-interplanetary ship dt system)
          (= :interstellar scope) (move-interstellar ship dt system))))

