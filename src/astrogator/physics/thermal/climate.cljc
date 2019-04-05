(ns astrogator.physics.thermal.climate
  (:require [astrogator.util.util :as u]))

(defrecord Climate [water-amount sea-level base-temp season-temp])

(defn adjusted-sea-level [base-temp water-amount]
  "-exp(x - 373) + 1"
  (let [boiling-temp 373
        evaporation (- 1.0 (Math/exp (* 0.05 (- base-temp boiling-temp))))]
    (max 0 (* evaporation water-amount))))

(defn climate [base-temp water-amount]
  (let [sea-level (adjusted-sea-level base-temp water-amount)]
    (->Climate water-amount sea-level base-temp base-temp)))

(defn calculate-temperature [tile sea-level base-temp]
  (let [ocean-depth (if (:ocean tile) (- sea-level (:height tile)) 0)
        height (if (:ocean tile) sea-level (:height tile))
        elevation-term (* 80 (- 0.5 (:elevation tile)))
        height-term (* 40 (- height 0.5))
        ocean-term (* 20 ocean-depth)]
    (max 0 (+ base-temp ocean-term height-term elevation-term))))

(defn init-oceans [tile-map sea-level]
  (let [update-ocean (fn [tile] (assoc-in tile [:ocean] (< (:height tile) sea-level)))]
    (u/update-values tile-map update-ocean)))

(defn init-temperature [tile-map sea-level base-temp]
  (u/update-values tile-map #(assoc-in % [:temperature] (calculate-temperature % sea-level base-temp))))

(defn init-glaciers [tile-map water-amount]
  (let [freeze-temp 273
        adjusted-freeze-temp (* water-amount freeze-temp)
        glacier? (fn [tile] (or (and (:ocean tile) (< (:temperature tile) freeze-temp))
                                (< (:temperature tile) adjusted-freeze-temp)))]
    (u/update-values tile-map #(assoc-in % [:glacier] (glacier? %)))))

(defn update-surface [tile-map climate]
  (-> tile-map
      (init-oceans (:sea-level climate))
      (init-temperature (:sea-level climate) (:season-temp climate))
      (init-glaciers (:water-amount climate))))

(defn update-climate [climate base-temp angle]
  (let [water-amount (:water-amount climate)
        season-temp (* base-temp (+ 1 (* 0.1 (Math/sin angle))))]
    (-> climate
        (assoc :base-temp base-temp)
        (assoc :season-temp season-temp)
        (assoc :sea-level (adjusted-sea-level season-temp water-amount)))))