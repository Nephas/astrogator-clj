(ns astrogator.gui.system
  (:require [astrogator.physics.trafo :as t]))

(defn get-planets-with-path [planets base-path]
  (let [indices (range (count planets))]
    (mapv #(assoc %1 :path (conj base-path :planets %2)) planets indices)))

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

(defn get-closest-star [system mappos]
  (apply min-key #(t/dist mappos (% :mappos)) (recur-stars-with-path system)))

(defn get-closest-planet-or-star [system mappos]
  (apply min-key #(t/dist mappos (% :mappos))
         (concat (recur-planets-with-path system)
                 (recur-stars-with-path system))))
