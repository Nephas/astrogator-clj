(ns astrogator.gui.camera
  (:require [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.state.selectors :as s]
            [astrogator.render.conf :as conf]
            [astrogator.generation.expandable :as exp]))

(defn change-refsystem [state distantsystem]
  (log/info "changing focus: " (:seed distantsystem))
  (-> state
      (assoc-in [:camera :sectorpos] (t/neg (:sectorpos distantsystem)))
      (assoc-in [:camera :refbody] nil)
      (assoc-in [:camera :refsystem] (:seed distantsystem))
      (assoc-in [:universe :refsystem] (exp/expand-if-possible distantsystem))))

(defn change-targetsystem [state distantsystem]
  (log/info "setting targetsystem: " (:seed distantsystem))
  (-> state
      (assoc-in [:camera :targetsystem] (:seed distantsystem))
      (assoc-in [:animation :target] 0)))

(defn change-refbody [state body]
  (let [full-path (into [] (concat [:universe :refsystem] (:path body)))]
    (log/info "changing focus: " full-path)
    (-> state
        (assoc-in [:camera :refbody] (:path body))
        (update-in full-path exp/expand-if-possible))))

(defn change-targetbody [state body]
  (log/info "select target: " (:path body))
  (-> state
      (assoc-in [:camera :targetbody] (:path body))
      (assoc-in [:animation :target] 0)))

(defn update-camera [state]
  (let [refbody (s/get-refbody state)
        refpos (get-in refbody [:mappos] [0 0])]
    (assoc-in state [:camera :mappos] (t/neg refpos))))

(defn get-scale [camera]
  (let [scale-before (camera :scale)
        nearer-than? #(> (camera :dist-zoom) (conf/thresholds %))
        scale-after (when-first [scale (filter #(nearer-than? %)
                                               (keys conf/thresholds))] scale)]
    (do (when (not= scale-after scale-before)
          (log/info "changed view-scale to: " scale-after))
        scale-after)))

(defn zoom [camera dir]
  (log/debug "zooming: " dir)
  (let [factor {:in  {:dist  2
                      :obj   1.25
                      :limit 1E+08}
                :out {:dist  0.5
                      :obj   0.8
                      :limit 1E-5}}
        calc-zoom (fn [zoom type] (float (* zoom (get-in factor [dir type]))))
        new-dist-zoom (calc-zoom (:dist-zoom camera) :dist)
        zoom? (not (or (and (= dir :in) (> new-dist-zoom (get-in factor [:in :limit])))
                       (and (= dir :out) (< new-dist-zoom (get-in factor [:out :limit])))))]
    (-> camera
        (update-in [:dist-zoom] (if zoom? #(calc-zoom % :dist) identity))
        (update-in [:obj-zoom] (if zoom? #(calc-zoom % :obj) identity))
        (assoc-in [:scale] (get-scale camera)))))

(defn on-screen? [[screen-x screen-y]]
  (and (< 0 screen-x (q/width))
       (< 0 screen-y (q/height))))