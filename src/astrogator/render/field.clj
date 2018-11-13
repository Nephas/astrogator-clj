(ns astrogator.render.field
  (:require [astrogator.render.geometry :as geo]
            [astrogator.util.color :as col]
            [astrogator.conf :as conf]
            [quil.core :as q]))

(defn get-grid
  ([x1 x2 y1 y2 d]
   (map (fn [y] (map (fn [x] (vector x y)) (range x1 x2 d))) (range y1 y2 d)))
  ([x y d] (get-grid 0 x 0 y d)))

(defn draw-grid [grid-list]
  (q/no-stroke)
  (col/fill [96 96 96])
  (doseq [pos grid-list]
    (geo/circle pos 1)))

(defn draw-field [system camera]
  (let [grid (get-grid (first conf/screen-size) (last conf/screen-size) 50)
        grid-list (apply concat grid)]
    (draw-grid grid-list)))

