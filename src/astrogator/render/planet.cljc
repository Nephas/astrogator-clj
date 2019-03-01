(ns astrogator.render.planet
  (:require [quil.core :as q]
            [astrogator.util.hex :as h]))

(defn draw-hex-wedge [base]
  (let [height (* 0.5 (Math/sqrt 3) base)]
    (q/quad 0 0 height (* 0.5 base) height (* -0.5 base) 0 (- base))))

(defn draw-hex [radius color]
  (do (apply q/fill color)
      (apply q/stroke color)
      (let [rad #(* Math/PI %)
            wedge #(q/with-rotation [%] (draw-hex-wedge radius))]
        (doall (map wedge (list 0 (rad (/ 2 3)) (rad (/ 4 3))))))))

(defn true-colors [tile colors]
  (let [height (:height tile)
        temp (:temperature tile)
        ice (:glacier tile)
        land (not (:ocean tile))
        ice-color (assoc (colors :glacier) 2 (+ 0.8 height))
        land-color (assoc (colors :rock) 2 (+ 0.25 height))
        ocean-color (assoc (colors :ocean) 2 (max 0.5 (+ 0.4 height)))]
    (cond ice ice-color
          land land-color
          true ocean-color)))

(defn draw-surface
  ([tiles colors zoom]
   (q/stroke-weight 1)
   (let [scale (* 0.1 zoom)
         view-tiles (filter #(:view %) tiles)
         colors (mapv #(true-colors % colors) view-tiles)
         positions (mapv #(h/cube-to-center-pix (:pos %) scale) view-tiles)]
     (doall (map (fn [pos col] (q/with-translation pos (draw-hex scale col))) positions colors)))))
