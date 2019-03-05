(ns astrogator.util.util
  (:require [quil.core :as q]))

(defn update-values [m f & args]
  (into {} (for [[k v] m] [k (apply f v args)])))

(defn update-list [m k f]
  (update-in m [k] #(mapv f %)))

(defn times [n f]
  (apply comp (repeat n f)))

(defn mean [vals]
  (/ (apply + vals) (count vals)))

(defn zip [l1 l2]
  (map #(list %1 %2) l1 l2))

(defn intersection [coll1 coll2]
  (let [id-coll1 (apply hash-map (flatten (zip coll1 coll1)))]
    (vals (select-keys id-coll1 coll2))))