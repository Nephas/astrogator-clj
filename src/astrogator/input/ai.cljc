(ns astrogator.input.ai
  (:require [astrogator.physics.trafo :as trafo]
            [astrogator.physics.move.transit :as t]
            [astrogator.util.log :as log]
            [astrogator.gui.selectors :as gs]
            [astrogator.state.selectors :as s]
            [astrogator.util.rand :as r]))

(defn find-target-planet [ship system]
  (let [possible-targets (rest (sort-by #(trafo/dist ship %)
                                  (map (fn [_] (gs/get-random-planet system))
                                       (range 6))))]
    (first possible-targets)))

(defn start-npc-transit [ship system] "TODO Fix me"
  (let [target (find-target-planet ship system)
        target-path (:path target)
        origin-path (get-in ship [:orbit :parent])
        origin (get-in system origin-path)
        offset (trafo/sub (:mappos ship) (:mappos origin))
        dist (trafo/dist origin target)]
    (let [transit (t/brachistochrone 0.1 dist origin-path target-path offset :interplanetary)]
      (do (log/info "ship " (:name ship) " on transit to: " target-path)
          (-> ship
              (assoc-in [:orbit] nil)
              (assoc-in [:ai-mode] :interplanetary)
              (assoc-in [:transit] transit))))))

(defn ai-decide [ship]
  (if (and (= :orbit (:ai-mode ship)) (zero? (r/rand-n 500)))
    (start-npc-transit ship (s/get-expanded-refsystem))
    ship))

(defn make-decisions [state]
  (update-in state [:universe :refsystem :ships] (fn [ships] (into [] (cons (first ships)
                                                                            (map ai-decide (rest ships)))))))