(ns astrogator.util.test)

(defn close-to
  ([a b threshold]
   (let [delta (Math/abs (- a b))]
     (< (/ delta a) threshold)))
  ([a b] (close-to a b 0.05)))