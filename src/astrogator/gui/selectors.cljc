(ns astrogator.gui.selectors
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.rand :as r]))

(defn get-closest-system [sector mappos]
  (apply min-key #(t/v-dist mappos (:sectorpos %)) sector))

(defn get-planets-with-path [planets base-path]
  (let [indices (range (count planets))]
    (mapv #(assoc %1 :path (conj base-path :planets %2)) planets indices)))

(defn get-ships-with-path [system]
  (let [indices (range (count (:ships system)))]
    (mapv #(assoc %1 :path [:ships %2]) (:ships system) indices)))

(defn recur-planets-with-path
  ([system path]
   (if (nil? (get system :body))
     (concat (recur-planets-with-path (system :compA) (conj path :compA))
             (recur-planets-with-path (system :compB) (conj path :compB))
             (get-planets-with-path (system :planets) path))
     (get-planets-with-path (system :planets) path)))
  ([system] (recur-planets-with-path system [])))

(defn recur-stars-with-path
  ([system path]
   (if (nil? (get system :body))
     (concat (recur-stars-with-path (system :compA) (conj path :compA))
             (recur-stars-with-path (system :compB) (conj path :compB)))
     [(assoc (system :body) :path (conj path :body))]))
  ([system] (recur-stars-with-path system [])))

(defn get-closest-planet-or-star
  ([system mappos cutoff]
   (let [get-dist #(t/v-dist mappos (:mappos %))
         ships (get-ships-with-path system)
         planets (recur-planets-with-path system)
         stars (recur-stars-with-path system)]
     (apply min-key #(:priority %)
            (concat (map #(assoc % :priority (+ cutoff (get-dist %))) ships)
                    (map #(assoc % :priority (+ cutoff (get-dist %))) planets)
                    (map #(assoc % :priority (get-dist %)) stars)))))
  ([system mappos]
   (get-closest-planet-or-star system mappos 0)))

(defn get-closest-planet [system mappos]
  (apply min-key #(t/v-dist mappos (:mappos %))
         (recur-planets-with-path system)))

(defn get-random-planet [system]
  (r/rand-coll (recur-planets-with-path system)))

(defn get-closest-star [system mappos]
  (apply min-key #(t/v-dist mappos (:mappos %))
         (recur-stars-with-path system)))

(defn get-parent-path [path]
  (if (number? (last path))
    (into [] (conj (pop (pop path)) :body))
    (into [] (conj (pop path) :system))))