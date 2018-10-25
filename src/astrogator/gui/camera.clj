(ns astrogator.gui.camera
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.render.conf :as conf]))

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

(defn get-scale [camera]
  (let [scale-before (camera :scale)
        scale-after (if (> (camera :dist-zoom) conf/system-thresh)
                      :system
                      :sector)]
    (do (when (not= scale-after scale-before)
          (log/info (str "changed view-scale to: " scale-after)))
        scale-after)))

(defn zoom [dir state]
  (let [factor {:in  {:dist 2
                      :obj  5/4}
                :out {:dist 1/2
                      :obj  4/5}}]
    (-> state
        (update-in [:camera :dist-zoom] #(* % ((factor dir) :dist)))
        (update-in [:camera :obj-zoom] #(* % ((factor dir) :obj)))
        (assoc-in [:camera :scale] (get-scale (state :camera))))))