(ns astrogator.generation.planet.map
  (:require [astrogator.util.hex :as h]
            [astrogator.physics.trafo :as t]))

(defrecord Tile [pos seed view elevation height temperature ocean glacier pressure])

(defn empty-tile [pos radius]
  (let [elevation (float (- 1 (/ (h/cube-dist pos [0 0 0]) radius)))
        view (< (int (t/dist [0 0] (h/cube-to-cart pos))) radius)]
    (->Tile pos false view elevation 0 0 false false 0)))

(defn init-tiles [radius]
  (let [radius-range (range (- radius) (inc radius))
        positions (map (fn [x] (map (fn [y] [x y (- (+ x y))]) radius-range)) radius-range)]
    (map (fn [pos] (empty-tile pos radius))
         (filter #(< (h/cube-dist % [0 0 0]) radius) (apply concat positions)))))

(defn init-map [tile-list]
  (into {} (map #(vector (:pos %1) %1) tile-list)))

(defn get-neighbors
  ([[x y z]] (let [idt identity
                   move (fn [dir] (mapv #(%1 %2) dir [x y z]))]
               (map move (list [inc dec idt]
                               [dec inc idt]
                               [idt inc dec]
                               [idt dec inc]
                               [inc idt dec]
                               [dec idt inc]))))
  ([tile-map pos] (vals (select-keys tile-map (get-neighbors pos)))))