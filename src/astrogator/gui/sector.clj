(ns astrogator.gui.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.generation.system :as sys]
            [astrogator.util.log :as log]))

(defn get-closest-system [sector mappos]
  (apply min-key #(t/dist mappos (% :sectorpos)) sector))

(defn change-viewsystem [state system]
  (log/info (str "setting viewsystem: " (system :seed)))
  (-> state
      (assoc-in [:camera :sectorpos] (t/neg (system :sectorpos)))
      (assoc-in [:camera :refbody] nil)
      (assoc-in [:universe :viewsystem] (sys/initiate-positions
                                          (sys/generate-system system)))))