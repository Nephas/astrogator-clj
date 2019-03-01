(ns astrogator.util.hex
  (:require [quil.core :as q]))

(defn cube-dist [pos1 pos2]
  (let [diff (mapv - pos1 pos2)
        abs (mapv #(Math/abs %) diff)]
    (/ (reduce + abs) 2)))

(defn cube-to-cart [[x y z]]
  (let [height (* 0.5 (Math/sqrt 3))
        base-x [height -0.5]
        base-y [(- height) -0.5]
        base-z [0 1]
        linear (fn [coord base] (mapv #(* % coord) base))]
    (mapv + (linear x base-x) (linear y base-y) (linear z base-z))))

(defn cube-to-pix [[x y z] scale]
  (mapv #(* % scale) (cube-to-cart [x y z])))

(defn cube-to-center-pix [[x y z] scale]
  (let [screen-center (mapv #(* 0.5 %) [(q/width) (q/height)])
        offset #(mapv + screen-center %)]
    (offset (cube-to-pix [x y z] scale))))
