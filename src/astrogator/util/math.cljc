(ns astrogator.util.math
  (:require [astrogator.util.util :as u]))

(defn expt [base pow]
  (Math/pow base pow))

(defn sqrt [x]
  (Math/sqrt x))

(defn gaussian [x]
  (Math/exp (- (* x x))))

(defn sample
  ([func val-range]
   (u/zip val-range (map func val-range)))
  ([func] (sample func (range 0.0 1.0 0.05))))

(defn integrate [tuples]
  (loop [total 0
         integrated nil
         remaining tuples]
    (if (empty? remaining) (reverse integrated)
                           (let [current (first remaining)
                                 new-total (+ total (last current))
                                 next-element (list (first current) new-total)]
                             (recur new-total
                                    (conj integrated next-element)
                                    (rest remaining))))))

(defn area [tuples]
  (reduce + (map #(last %) tuples)))

(defn normalize [tuples]
  (let [normalize #(* % (/ 1 (area tuples)))]
    (map #(list (first %) (normalize (last %))) tuples)))

