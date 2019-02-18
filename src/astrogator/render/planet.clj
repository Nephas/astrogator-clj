(ns astrogator.render.planet
  (:require [quil.core :as q]
            [astrogator.util.hex :as h]))

(defn draw-hex-wedge [base]
  (let [height (* 1/2 (Math/sqrt 3) base)]
    (q/quad 0 0 height (* 1/2 base) height (* -1/2 base) 0 (- base))))

(defn draw-hex [radius color]
  (do (apply q/fill color)
      (apply q/stroke color)
      (let [rad #(* Math/PI %)
            wedge #(q/with-rotation [%] (draw-hex-wedge radius))]
        (doall (map wedge (list 0 (rad 2/3) (rad 4/3)))))))

(defn true-colors [tile]
  (let [height (:height tile)
        temp (:temperature tile)
        frozen (:glacier tile)
        land (not (:ocean tile))]
    (cond frozen [200 200 255]
          land [(* 255 height) (* 255 height) 64]
          true [64 64 (+  128 (* 128 height))])))

(defn draw-surface
  ([tiles zoom]
   (q/stroke-weight 1)
   (let [scale (* 0.1 zoom)
         view-tiles (filter #(:view %) tiles)
         colors (mapv true-colors view-tiles)
         positions (mapv #(h/cube-to-center-pix (:pos %) scale) view-tiles)]
     (doall (map (fn [pos col] (q/with-translation pos (draw-hex scale col))) positions colors)))))
