(ns astrogator.util.color
  (:require [quil.core :as q]))

(defn color-to-vec [col]
  [(q/hue col) (q/saturation col) (q/brightness col)])

(defn blend-vec-color
  ([col1 col2] (color-to-vec (q/blend-color (apply q/color col1) (apply q/color col2) :blend)))
  ([col1 col2 col3] (color-to-vec (q/blend-color (apply q/color col1)
                      (q/blend-color (apply q/color col2) (apply q/color col3) :multiply) :screen))))

(defn fill
  ([vec] (q/fill (apply q/color vec)))
  ([vec alpha] (q/fill (apply q/color vec) alpha)))