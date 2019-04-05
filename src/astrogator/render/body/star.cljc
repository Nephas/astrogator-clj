(ns astrogator.render.body.star
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.render.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.render.tilemap :as tm]
            [astrogator.util.hex :as h]))

(defn true-colors [tile color]
  (assoc color 2 (:temp tile)))

(defn draw-surface
  ([tiles color zoom rot]
   (q/stroke-weight 1)
   (let [scale (* 0.1 zoom)
         view-tiles (filter #(:view %) tiles)
         colors (mapv #(true-colors % color) view-tiles)
         positions (mapv #(h/cube-to-center-pix (:pos %) scale rot) view-tiles)]
     (doall (map (fn [pos col] (q/with-translation pos (tm/draw-hex scale col rot))) positions colors)))))

(defn draw-star
  ([star camera]
   (let [pos (t/map-to-screen (:mappos star) camera)
         size (* 5 (camera :obj-zoom) (:radius star))
         color (:color star)
         rot (get-in star [:rotation :angle])]
     (do (col/fill color)
         (do (draw-surface (vals (:surface star)) color (* 0.5 size) rot)
             (geo/ring pos (* 1.6 size) (assoc color 2 0.66) (* 0.2 size)))))))