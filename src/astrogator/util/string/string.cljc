(ns astrogator.util.string.string
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.string.format :as fmt]))

(defn cut [s size]
  (if (> (count s) size) (str (subs s 0 size) "…") s))

(defn fmt-round [x]
  (if (< 0.01 (Math/abs x) 1000)
    (fmt/f-str "~,2f" x)
    (fmt/f-str "~,1e" x)))

(defn fmt-vec [[x y]]
  (str "[" (fmt-round x) ", " (fmt-round y) "]"))

(defn fmt-numeric [x]
  (cond (t/vec2d? x) (fmt-vec x)
        (float? x) (fmt-round x)
        true (cut (str x) 20)))

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s] (if (re-find #"^-?\d+\.?\d*$" s)
        (read-string s)))

(defn join
  ([coll del] (reduce #(str %1 del %2) coll))
  ([coll] (join coll "")))
