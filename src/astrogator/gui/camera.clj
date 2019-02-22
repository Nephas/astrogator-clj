(ns astrogator.gui.camera
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.util.selectors :as s]
            [astrogator.render.conf :as conf]
            [astrogator.conf :as c]
            [astrogator.generation.expandable :as exp]))

(defn change-focus [state body]
  (let [full-path (into [] (concat [:universe :viewsystem] (:path body)))]
    (log/info (str "changing focus: " full-path))
    (-> state
        (assoc-in [:camera :refbody] (:path body))
        (update-in full-path exp/expand-if-possible))))

(defn change-target [state body]
  (log/info (str "select target: " (:path body)))
  (-> state
      (assoc-in [:camera :target] (:path body))
      (assoc-in [:animation :target] 0)))

(defn update-camera [state]
  (let [refbody (s/get-refbody state)
        refpos (get-in refbody [:mappos] [0 0])]
    (assoc-in state [:camera :mappos] (t/neg refpos))))

(defn update-playership [state]
  (let [shippos ((s/get-playership state) :mappos)
        mousepos (get-in state [:camera :mouse :mappos])
        diff (t/sub mousepos shippos)
        pointing (+ (* 1/2 Math/PI) (- ((t/cart-to-pol diff) 1)))]
    (assoc-in state (conj s/playership-path :pointing) pointing)))

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

(defn on-screen? [[screen-x screen-y]]
  (and (< 0 screen-x (c/screen-size 0))
       (< 0 screen-y (c/screen-size 1))))