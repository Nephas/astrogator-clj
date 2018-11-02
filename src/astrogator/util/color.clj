(ns astrogator.util.color
  (:require [quil.core :as q]))

(defn vec-to-color [vec]
    (q/color (vec 0) (vec 1) (vec 2)))

(defn blend-vec-color [col1 col2]
  (mapv #(/ (+ %1 %2) 2) col1 col2))

(defn fill
  ([vec] (q/fill (vec-to-color vec)))
  ([vec alpha] (q/fill (vec-to-color vec) alpha)))