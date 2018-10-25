(ns astrogator.util.util
  (:require [quil.core :as q]))

(defn vec-to-color [vec]
  (q/color (vec 0) (vec 1) (vec 2)))