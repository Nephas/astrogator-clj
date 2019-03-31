(ns astrogator.gui.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.generation.expandable :as exp]
            [astrogator.generation.player :as pl]
            [astrogator.util.selectors :as s]))

(defn get-closest-system [sector mappos]
  (apply min-key #(t/dist mappos (:sectorpos %)) sector))

(defn change-refsystem [state distantsystem]
  (log/info "setting refsystem: " (:seed distantsystem))
  (let [ship (s/get-playership state)]
    (-> state
        (assoc-in [:camera :sectorpos] (t/neg (:sectorpos distantsystem)))
        (assoc-in [:camera :refbody] nil)
        (assoc-in [:camera :refsystem] (:seed distantsystem))
        (assoc-in [:universe :refsystem] (-> distantsystem
                                             (exp/expand-if-possible)
                                             (pl/place-playership ship))))))

(defn change-targetsystem [state distantsystem]
  (log/info "setting targetsystem: " (:seed distantsystem))
  (-> state
      (assoc-in [:camera :targetsystem] (:seed distantsystem))
      (assoc-in [:animation :target] 0)))