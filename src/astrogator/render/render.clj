(ns astrogator.render.render
  (:require [astrogator.render.system :as sys]
            [astrogator.render.sector :as sec]
            [quil.core :as q]
            [astrogator.util.log :as log]))

(defn get-scale [camera]
  (let [scale-before (camera :scale)
        scale-after (if (> (camera :dist-zoom) 1)
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

(defn render-universe [universe camera]
  (q/background 50 0 75)
  (if (= :system (camera :scale))
    (sys/draw-stars (universe :viewsystem) camera)
    (sec/draw-sector (universe :sector) (universe :clouds) camera)))

(defn vec-to-color [vec]
  (q/color (vec 0) (vec 1) (vec 2)))