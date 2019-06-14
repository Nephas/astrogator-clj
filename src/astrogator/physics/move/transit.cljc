(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.log :as log]
            [astrogator.physics.units :as u]
            [astrogator.state.selectors :as s]
            [astrogator.gui.selectors :as sel]))

(declare move-on-trajectory)

(defrecord Trajectory [par parvel paracc parlength origin target offset scope])

(defn brachistochrone [g-acc length origin target offset scope]
  (->Trajectory 0 0 (u/conv g-acc :g :AU/d2) length origin target offset scope))

(defn move-interplanetary [ship dt system]
  (let [{par         :par
         origin-path :origin
         target-path :target
         offset      :offset
         parlength   :parlength} (:transit ship)
        origin (get-in system origin-path)
        target (get-in system target-path)]
    (if (< par parlength)
      (move-on-trajectory ship dt (:mappos origin) (:mappos target) offset)
      (o/place-in-orbit ship target-path))))

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
                                    (o/place-in-orbit ship (:path targetplanet)))
          true (-> ship
                   (move-on-trajectory dt (t/neg targetpos) [0 0] offset)
                   (assoc :swapsystem target-seed)))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos offset]
  (let [{par       :par
         parvel    :parvel
         paracc    :paracc
         parlength :parlength} (:transit ship)
        origin-mappos (t/add origin-mappos offset)
        new-parvel (if (< par (* 0.5 parlength))
                     (+ parvel (* dt paracc))
                     (- parvel (* dt paracc)))
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

