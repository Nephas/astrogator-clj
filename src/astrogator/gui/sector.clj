(ns astrogator.gui.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.generation.system :as sys]
            [astrogator.util.log :as log]
            [astrogator.generation.distantsystem :as ds]))

(defn get-closest-system [sector mappos]
  (apply min-key #(t/dist mappos (:sectorpos %)) sector))

(defn change-viewsystem [state distantsystem]
  (log/info (str "setting viewsystem: " (:seed distantsystem)))
  (-> state
      (assoc-in [:camera :sectorpos] (t/neg (:sectorpos distantsystem)))
      (assoc-in [:camera :refbody] nil)
      (assoc-in [:universe :viewsystem] (-> distantsystem
                                            (ds/expand)
                                            (sys/place-playership [10 10])))))