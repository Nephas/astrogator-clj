(ns astrogator.util.color
  (:require [quil.core :as q]))

(defn with-alpha
  ([color a] (if (= (count color) 3)
               (conj color a)
               (assoc color 3 a))))

(defn color-to-vec [col]
  [(q/hue col) (q/saturation col) (q/brightness col)])

(defn blend-vec-color
  ([col1 col2] (color-to-vec (q/blend-color (apply q/color col1) (apply q/color col2) :soft-light)))
  ([col1 col2 col3] (color-to-vec (q/blend-color (apply q/color col1)
                      (q/blend-color (apply q/color col2) (apply q/color col3) :hard-light) :hard-light))))

(defn fill
  ([vec] (q/fill (apply q/color vec)))
  ([vec alpha] (q/fill (apply q/color vec) alpha))
  ([] (fill [0 0 0] 0)))