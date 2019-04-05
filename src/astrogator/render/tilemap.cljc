(ns astrogator.render.tilemap
  (:require [quil.core :as q]
            [astrogator.util.color :as col]))


(defn draw-hex-wedge [base]
  (let [height (* 0.5 (Math/sqrt 3) base)]
    (q/quad 0 0 height (* 0.5 base) height (* -0.5 base) 0 (- base))))

(defn draw-hex [radius color rot]
  (do (col/fill color)
      (apply q/stroke color)
      (let [rad #(* Math/PI %)
            wedge #(q/with-rotation [(+ rot %)] (draw-hex-wedge radius))]
        (doall (map wedge (list 0 (rad (/ 2 3)) (rad (/ 4 3))))))))