(ns astrogator.generation.body.surface
  (:require [astrogator.util.util :as u]
            [astrogator.generation.body.tilemap :as m]
            [astrogator.util.rand :as r]
            [astrogator.poetry.haiku :as h]            ))

(defn evolve-tile [tile tile-map update-key update-procedure]
  (let [pos (:pos tile)
        neighborhood (m/get-neighbors tile-map pos)]
    (assoc-in tile [update-key] (update-procedure tile neighborhood))))

(defn shape-evolve-tile [tile tile-map]
  (let [update-procedure (fn [tile neighborhood]
                           (<= 4 (count (filter #(:seed %) (conj neighborhood tile)))))]
    (evolve-tile tile tile-map :seed update-procedure)))

(defn init-seeds [tile-map prob]
  (let [seed (fn [] (< (r/uniform 1) prob))]
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
  (let [factor (r/uniform (- 1 scatter) (+ 1 scatter))]
    (update-in tile [:height] #(* factor %))))

(defn noisify-height [tile-map scatter]
  (u/update-values tile-map noise-evolve-tile scatter))

(defn adjusted-water-amount [base-temp water-amount]
  "-exp(x - 373) + 1"
  (let [boiling-temp 373
        evaporation (- 1.0 (Math/exp (* 0.05 (- base-temp boiling-temp))))]
    (max 0 (* evaporation water-amount))))

(defn init-oceans [tile-map sea-level]
  (let [update-ocean (fn [tile] (assoc-in tile [:ocean] (< (:height tile) sea-level)))]
    (u/update-values tile-map update-ocean)))

(defn calculate-temperature [tile sea-level base-temp]
  (let [ocean-depth (if (:ocean tile) (- sea-level (:height tile)) 0)
        height (if (:ocean tile) sea-level (:height tile))
        elevation-term (* 80 (- 0.5 (:elevation tile)))
        height-term (* 40 (- height 0.5))
        ocean-term (* 20 ocean-depth)]
    (max 0 (+ base-temp ocean-term height-term elevation-term))))

(defn init-temperature [tile-map sea-level base-temp]
  (u/update-values tile-map #(assoc-in % [:temperature] (calculate-temperature % sea-level base-temp))))

(defn init-glaciers [tile-map water-amount]
  (let [freeze-temp 273
        adjusted-freeze-temp (* water-amount freeze-temp)
        glacier? (fn [tile] (or (and (:ocean tile) (< (:temperature tile) freeze-temp))
                                (< (:temperature tile) adjusted-freeze-temp)))]
    (u/update-values tile-map #(assoc-in % [:glacier] (glacier? %)))))

(defn cellular-map [size shape-prob shape-steps height-steps noise-range water-amount base-temp]
  (let [init-map (m/init-map (m/init-tiles size))
        sea-level (adjusted-water-amount base-temp water-amount)]
    (-> init-map
        (init-seeds shape-prob)
        (smooth-shapes shape-steps)
        (init-height)
        (smooth-height height-steps)
        (noisify-height noise-range)
        (init-oceans sea-level)
        (init-temperature sea-level base-temp)
        (init-glaciers water-amount))))

(defn get-descriptors [water-amount base-temp base-flux circumbinary]
  (let [sea-level (adjusted-water-amount base-temp water-amount)
        frozen (< base-temp 223)
        molten (> base-temp 473)
        dark (< base-flux 10)
        bright (> base-flux 10000)
        wet (and (not molten) (not frozen) (> sea-level 0.7))
        dry (and (not molten) (< sea-level 0.3))
        tags (into [] (filter #(not (nil? %))
                              [(if circumbinary :multiple :single)
                               (if (< 273 base-temp 373) :habitable :hostile)
                               (if dark :dark)
                               (if bright :bright)
                               (if frozen :frozen) (if molten :molten)
                               (if wet :wet) (if dry :dry)
                               ]))]
    {:sea-level sea-level
     :tags      tags
     :poem      (h/generate-haiku tags)}))
