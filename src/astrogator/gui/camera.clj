(ns astrogator.gui.camera
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.render.conf :as conf]))

(defn change-focus [state body]
  (log/info (str "changing focus: " (body :path)))
  (-> state
      (assoc-in [:camera :refbody] (body :path))))

(defn get-refbody [camera viewsystem]
  (let [path (camera :refbody)]
    (get-in viewsystem path)))

(defn update-camera [state]
  (let [refbody (get-refbody (state :camera) (get-in state [:universe :viewsystem]))
        refpos (get-in refbody [:mappos] [0 0])]
    (assoc-in state [:camera :mappos] (t/neg refpos))))

(defn get-scale [camera]
  (let [scale-before (camera :scale)
        nearer-than? #(> (camera :dist-zoom) (conf/thresholds %))
        scale-after (when-first [scale (filter #(nearer-than? %)
                                               (keys conf/thresholds))] scale)]
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