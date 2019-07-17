(ns astrogator.render.tilemap
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.util.hex :as h]
            [astrogator.generation.body.tilemap :as m]))


(defn draw-hex-wedge [base]
  (let [height (* 0.5 (Math/sqrt 3) base)]
    (q/quad 0 0 height (* 0.5 base) height (* -0.5 base) 0 (- base))))

(defn draw-hex [radius color rot]
  (do (col/fill color)
      (apply q/stroke color)
      (let [rad #(* Math/PI %)
            wedge #(q/with-rotation [(+ rot %)] (draw-hex-wedge radius))]
        (doall (map wedge (list 0 (rad (/ 2 3)) (rad (/ 4 3))))))))

(defn draw-tilemap [body scale]
  (let [rot (get-in body [:rotation :angle])
        view-tiles (filter #(:view %) (vals (:surface body)))
        colors (mapv #(m/true-color % body) view-tiles)
        positions (mapv #(h/cube-to-center-pix (:pos %) scale rot) view-tiles)]
    (q/stroke-weight 1)
    (doall (map (fn [pos col] (q/with-translation pos (draw-hex scale col rot))) positions colors))))