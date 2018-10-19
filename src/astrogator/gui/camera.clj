(ns astrogator.gui.camera
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]))

(defn change-focus [state body]
  (log/info (str "changing focus: " body))
  (-> state
      (assoc-in [:camera :refbody] (body :path))))

(defn update-camera [state]
  (let [camera (state :camera)
        path (camera :refbody)
        viewsystem (get-in state [:universe :viewsystem])
        refpos (if (nil? path) [0 0]
                               (get-in viewsystem (conj path :mappos)))]
    (assoc-in state [:camera :mappos] (t/neg refpos))))