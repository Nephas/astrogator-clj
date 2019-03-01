(ns astrogator.util.string
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            ))

(defn format
  "Formats a string using goog.string.format."
  [fmt & args]
  fmt)

(defn fmt-round [x]
  (if (and (< x 1000) (> x 0.01))
    (format "%.2f" x)
    (format "%.1E" x)))

(defn fmt-vec [[x y]]
  (format "[%s, %s]"
          (if (int? x) x (fmt-round x))
          (if (int? y) y (fmt-round y))))

(defn fmt-numeric [x]
  (cond (t/vec2d? x) (fmt-vec x)
        (float? x) (fmt-round x)
        ;(rational? x) (fmt-round (float x))
        true (str x)))

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s] (if (re-find #"^-?\d+\.?\d*$" s)
        (read-string s)))
