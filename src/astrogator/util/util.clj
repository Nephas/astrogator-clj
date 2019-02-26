(ns astrogator.util.util)

(defn update-values [m f & args]
  (into {} (for [[k v] m] [k (apply f v args)])))

(defn update-list [m k f]
             (update-in m [k] #(mapv f %)))

(defn times [n f]
  (apply comp (repeat n f)))

(defn mean [vals]
  (/ (apply + vals) (count vals)))