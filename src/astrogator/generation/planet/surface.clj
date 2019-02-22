(ns astrogator.generation.planet.surface
  (:require [astrogator.util.util :as u]
            [astrogator.generation.planet.tilemap :as m]
            [astrogator.util.rand :as r]))

(defn evolve-tile [tile tile-map update-key update-procedure]
  (let [pos (:pos tile)
        neighborhood (m/get-neighbors tile-map pos)]
    (assoc-in tile [update-key] (update-procedure tile neighborhood))))

(defn shape-evolve-tile [tile tile-map]
  (let [update-procedure (fn [tile neighborhood]
                           (<= 4 (count (filter #(:seed %) (conj neighborhood tile)))))]
    (evolve-tile tile tile-map :seed update-procedure)))

(defn init-seeds [tile-map prob]
  (let [seed (fn [] (< (r/rand 1) prob))]
    (u/update-values tile-map #(assoc-in % [:seed] (seed)))))

(defn shape-step [tile-map]
  (u/update-values tile-map shape-evolve-tile tile-map))

(defn smooth-shapes [tile-map n]
  ((u/times n shape-step) tile-map))


(defn height-evolve-tile [tile tile-map]
  (let [update-procedure (fn [tile neighborhood]
                           (float (u/mean (map #(:height %) (conj neighborhood tile)))))]
    (evolve-tile tile tile-map :height update-procedure)))

(defn height-step [tile-map]
  (u/update-values tile-map height-evolve-tile tile-map))

(defn init-height [tile-map]
  (let [height (fn [seed] (if seed 1 0))]
    (u/update-values tile-map #(assoc-in %1 [:height] (height (:seed %1))))))

(defn smooth-height [tile-map n]
  ((u/times n height-step) tile-map))


(defn noise-evolve-tile [tile scatter]
  (let [factor (r/rand-range (- 1 scatter) (+ 1 scatter))]
    (update-in tile [:height] #(* factor %))))

(defn noisify-height [tile-map scatter]
  (u/update-values tile-map noise-evolve-tile scatter))

(defn init-oceans [tile-map sea-level]
  (let [update-ocean (fn [tile] (assoc-in tile [:ocean] (< (:height tile) sea-level)))]
    (u/update-values tile-map update-ocean)))

(defn calculate-temperature [tile sea-level base-heat]
  (let [ocean-depth (if (:ocean tile) (- sea-level (:height tile)) 0)
        height (if (:ocean tile) sea-level (:height tile))]
    (+ base-heat (* ocean-depth 0.2) (- 1 (* 0.5 (Math/abs (:elevation tile))) (* 0.5 height)))))

(defn init-temperature [tile-map sea-level base-heat]
  (u/update-values tile-map #(assoc-in % [:temperature] (calculate-temperature % sea-level base-heat))))

(defn init-glaciers [tile-map freeze-temp]
  (u/update-values tile-map #(assoc-in % [:glacier] (< (:temperature %) freeze-temp))))

(defn cellular-map [size shape-prob shape-steps height-steps noise-range sea-level base-heat freeze-temp]
  (let [init-map (m/init-map (m/init-tiles size))]
    (-> init-map
        (init-seeds shape-prob)
        (smooth-shapes shape-steps)
        (init-height)
        (smooth-height height-steps)
        (noisify-height noise-range)
        (init-oceans sea-level)
        (init-temperature sea-level base-heat)
        (init-glaciers freeze-temp))))