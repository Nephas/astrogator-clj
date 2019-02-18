(ns astrogator.util.util)

(defn update-values [m f & args]
  (into {} (for [[k v] m] [k (apply f v args)])))

(defn times [n f]
  (apply comp (repeat n f)))

(defn mean [vals]
  (/ (apply + vals) (count vals)))