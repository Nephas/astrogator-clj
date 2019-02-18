(ns astrogator.util.string
  (:require [astrogator.physics.trafo :as t]))

(defn fmt-round [x]
  (if (and (< x 1000) (> x 0.01))
    (format "%.2f" x)
    (format "%.1E" x)))

(defn fmt-vec [[x y]]
  (format "[%s, %s]" (fmt-round x) (fmt-round y)))

(defn fmt-numeric [x]
  (cond (t/vec2d? x) (fmt-vec x)
        (float? x) (fmt-round x)
        true (str x)))
