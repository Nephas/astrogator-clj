(ns astrogator.physics.move.transit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.move.orbit :as o]
            [astrogator.util.log :as log]
            [astrogator.physics.units :as u]
            [astrogator.state.selectors :as s]
            [astrogator.gui.selectors :as sel]))

(defprotocol Transit
  (transit-move [this dt origin-mappos target-mappos]))

(defrecord Trajectory [par parvel paracc parlength origin target])

(defn brachistochrone [g-acc length origin target]
  (->Trajectory 0 0 (u/conv g-acc :g :AU/d2) length origin target))

(defn move-interplanetary [ship dt system]
  (let [{par         :par
         origin-path :origin
         target-path :target
         parlength   :parlength} (:interplanetary ship)
        origin (get-in system origin-path)
        target (get-in system target-path)]
    (if (< par parlength)
      (transit-move ship dt (:mappos origin) (:mappos target))
      (o/place-in-orbit ship target-path (get-in system target-path)))))

(defn move-interstellar [ship dt system]
  (let [{par        :par
         origin     :origin
         target     :target
         targetseed :targetseed
         parlength  :parlength} (:interstellar ship)]
    (cond (< par (* 0.5 parlength)) (transit-move ship dt origin target)
          (> par (* 1 parlength)) (let [targetplanet (sel/get-closest-planet system [0 0])]
                                    (-> ship
                                        (o/place-in-orbit (:path targetplanet) targetplanet)
                                        (dissoc :swapsystem)))
          true (if (false? (:swapsystem ship))
                 (transit-move ship dt origin target)
                 (-> ship (assoc :swapsystem targetseed)
                     (assoc-in [:interstellar :origin] (t/neg target))
                     (assoc-in [:interstellar :target] [0 0]))))))

(defn move-on-trajectory [ship dt origin-mappos target-mappos]
  (let [ai-mode (:ai-mode ship)
        {par       :par
         parvel    :parvel
         paracc    :paracc
         parlength :parlength} (ai-mode ship)
        new-parvel (if (< par (* 0.5 parlength))
                     (+ parvel (* dt paracc))
                     (- parvel (* dt paracc)))
        new-par (+ (* parvel dt) par)
        progress (/ new-par parlength)
        mappos-progress (t/scalar progress (t/sub target-mappos origin-mappos))]
    (-> ship
        (assoc-in [ai-mode :par] new-par)
        (assoc-in [ai-mode :parvel] new-parvel)
        (assoc-in [:mappos] (t/add origin-mappos mappos-progress)))))

(defn start-interstellar [ship target origin]
  (let [dist (t/dist (:sectorpos target)
                     (:sectorpos origin))
        targetpos (t/sub (:sectorpos target) (:sectorpos origin))]
    (-> ship
        (assoc-in [:orbit] nil)
        (assoc-in [:ai-mode] :interstellar)
        (assoc-in [:interstellar] (brachistochrone 0.01 dist (:mappos ship) targetpos))
        (assoc-in [:interstellar :targetseed] (:seed target)))))

(defn start-interplanetary [ship target-path origin-path]
  (let [system (s/get-expanded-refsystem)
        dist (t/dist (get-in system (conj origin-path :mappos))
                     (get-in system (conj target-path :mappos)))]
    (-> ship
        (assoc-in [:orbit] nil)
        (assoc-in [:ai-mode] :interplanetary)
        (assoc-in [:interplanetary] (brachistochrone 0.01 dist origin-path target-path)))))
