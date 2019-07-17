(ns astrogator.generation.body.tilemap
  (:require [astrogator.util.hex :as h]
            [astrogator.physics.trafo :as t]))

(defprotocol TileColor "provide colors for rendering derived from tile attributes"
  (true-color [this body] "get the visible light color for rendering"))

(defrecord StarTile [pos seed view elevation temp]
  TileColor
  (true-color [this star]
    (assoc (:color star) 2 (:temp this))))

(defrecord PlanetTile [pos seed view elevation height temp ocean glacier]
  TileColor
  (true-color [this planet]
    (let [{height :height
           ice    :glacier} this
          land (not (:ocean this))
          ice-color (assoc (:glacier (:color planet)) 2 (+ 0.8 height))
          land-color (assoc (:rock (:color planet)) 2 (+ 0.25 height))
          ocean-color (assoc (:ocean (:color planet)) 2 (max 0.5 (+ 0.4 height)))]
      (cond ice ice-color
            land land-color
            true ocean-color))))

(defn star-tile [pos radius]
  (let [elevation (float (- 1 (/ (h/cube-dist pos [0 0 0]) radius)))
        view (< (int (t/v-dist [0 0] (h/cube-to-cart pos))) radius)]
    (->StarTile pos false view elevation 0)))

(defn planet-tile [pos radius]
  (let [elevation (float (- 1 (/ (h/cube-dist pos [0 0 0]) radius)))
        view (< (int (t/v-dist [0 0] (h/cube-to-cart pos))) radius)]
    (->PlanetTile pos false view elevation 0 0 false false)))

(defn init-tiles [tile-constructor radius]
  (let [radius-range (range (- radius) (inc radius))
        positions (map (fn [x] (map (fn [y] [x y (- (+ x y))]) radius-range)) radius-range)]
    (map (fn [pos] (tile-constructor pos radius))
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