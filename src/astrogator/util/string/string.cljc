(ns astrogator.util.string.string
  (:require [astrogator.physics.trafo :as t]
            [clojure.string :as s]
            [astrogator.util.string.format :as fmt]))

(declare fmt-generic)

(defn cut [s size]
  (if (> (count s) size) (str (subs s 0 size) "…") s))

(defn fmt-round [x]
  (if (< 0.01 (Math/abs x) 1000)
    (fmt/f-str "~,2f" x)
    (fmt/f-str "~,1e" x)))

(defn fmt-vec [[x y]]
  (str "[" (fmt-round x) ", " (fmt-round y) "]"))

(defn fmt-map [x]
  (str "{" (first (keys x)) " " (fmt-generic (first (vals x))) " …}"))

(defn fmt-list [x]
  (str "[" (fmt-generic (first x)) " …]"))

(defn fmt-generic [x]
  (cond (or (map? x) (record? x)) (fmt-map x)
        (t/vec2d? x) (fmt-vec x)
        (float? x) (fmt-round x)
        (or (list? x) (vector? x)) (fmt-list x)
        true (cut (str x) 20)))

(defn join
  ([coll del] (reduce #(str %1 del %2) coll))
  ([coll] (join coll "")))

()