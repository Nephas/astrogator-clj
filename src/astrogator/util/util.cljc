(ns astrogator.util.util)

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