(ns astrogator.gui.system
  (:require [astrogator.physics.trafo :as t]))

(defn get-stars-with-path
  ([system path]
   (if (nil? (get system :star))
     (flatten [(get-stars-with-path (system :compA) (conj path :compA))
               (get-stars-with-path (system :compB) (conj path :compB))])
     [(assoc (system :star) :path (conj path :star))]))
  ([system] (get-stars-with-path system [])))

(defn get-closest-star [system mappos]
  (apply min-key #(t/dist mappos (% :mappos)) (get-stars-with-path system)))

