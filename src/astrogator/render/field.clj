(ns astrogator.render.field
  (:require [astrogator.render.geometry :as geo]
            [astrogator.util.color :as col]
            [astrogator.conf :as conf]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.physics.gravity :as g]
            [astrogator.util.log :as log]))

(defn get-grid
  ([x1 x2 y1 y2 d]
   (map (fn [y] (map (fn [x] (vector x y)) (range x1 x2 d))) (range y1 y2 d)))
  ([x y d] (get-grid 0 x 0 y d)))

(defn draw-grid [grid-arrows]
  (q/stroke-weight 1.5)
  (doseq [entry grid-arrows]
    (geo/arrow (first entry) (last entry))))

(defn draw-gravity-field [system camera]
  (let [arrow (fn [pos] (g/acc-at-pos (t/screen-to-map pos camera) system))
        grid (get-grid (first conf/screen-size) (last conf/screen-size) 30)
        grid-arrows (map #(list %1 (arrow %1)) (apply concat grid))]
    (q/stroke 96 64 96)
    (draw-grid grid-arrows)))

(defn draw-radiation-field [system camera]
  (let [arrow (fn [pos] (g/flux-at-pos (t/screen-to-map pos camera) system))
        grid (get-grid (first conf/screen-size) (last conf/screen-size) 30)
        grid-arrows (map #(list %1 (arrow %1)) (apply concat grid))]
    (q/stroke 96 96 64)
    (draw-grid grid-arrows)))
