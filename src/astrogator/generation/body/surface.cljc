(ns astrogator.generation.body.surface
  (:require [astrogator.util.util :as u]
            [astrogator.generation.body.tilemap :as m]
            [astrogator.util.rand :as r]
            [astrogator.poetry.haiku :as h]
            [astrogator.physics.thermal.climate :as c]))

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

(defn cellular-map [size shape-prob shape-steps height-steps noise-range]
  (let [init-map (m/init-map (m/init-tiles size))]
    (-> init-map
        (init-seeds shape-prob)
        (smooth-shapes shape-steps)
        (init-height)
        (smooth-height height-steps)
        (noisify-height noise-range))))

(defn get-descriptors [climate base-flux circumbinary]
  (let [{base-temp    :base-temp
         sea-level    :sea-level} climate
        frozen (< base-temp 223)
        molten (> base-temp 473)
        dark (< base-flux 10)
        bright (> base-flux 10000)
        wet (and (not molten) (not frozen) (> sea-level 0.7))
        dry (and (not molten) (< sea-level 0.3))
        tags (into [] (filter some?
                              [(if circumbinary :multiple :single)
                               (if (< 273 base-temp 373) :habitable :hostile)
                               (if dark :dark)
                               (if bright :bright)
                               (if frozen :frozen) (if molten :molten)
                               (if wet :wet) (if dry :dry)
                               ]))]
    {:tags tags
     :poem (h/generate-haiku tags)}))
