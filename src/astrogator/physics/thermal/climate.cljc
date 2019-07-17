(ns astrogator.physics.thermal.climate
  (:require [astrogator.util.rand :as r]))

(defrecord Climate [water-amount sea-level base-temp season-temp])

(defn adjusted-sea-level [base-temp water-amount]
  "-exp(x - 373) + 1"
  (let [boiling-temp 373
        evaporation (- 1.0 (Math/exp (* 0.05 (- base-temp boiling-temp))))]
    (max 0 (* evaporation water-amount))))

(defn generate-climate [mass]
  (if (< mass 10)
    (let [water-amount (r/uniform 0.05 0.95)
          sea-level (adjusted-sea-level 0 water-amount)]
      (->Climate water-amount sea-level 0 0))
    (->Climate 0 0 0 0)))

(defn calculate-temperature [tile sea-level base-temp]
  (let [{ocean     :ocean
         height    :height
         elevation :elevation} tile
        ocean-depth (if ocean (- sea-level height) 0)
        ocean-term (* 20 ocean-depth)
        elevation-term (* 80 (- 0.5 elevation))
        height-term (* 40 (- (if ocean sea-level height) 0.5))]
    (assoc tile :temp (max 0 (+ base-temp ocean-term height-term elevation-term)))))

(defn update-ocean [tile sea-level]
  (assoc tile :ocean (< (:height tile) sea-level)))

(defn update-glacier [tile water-amount]
  (let [ocean-freeze 273
        land-freeze (* water-amount 273)]
    (assoc tile :glacier (or (and (:ocean tile) (< (:temp tile) ocean-freeze))
                             (< (:temp tile) land-freeze)))))

(defn update-tile [tile climate]
  (let [{water-amount :water-amount
         base-temp    :season-temp
         sea-level    :sea-level} climate]
    (-> tile
        (calculate-temperature sea-level base-temp)
        (update-ocean sea-level)
        (update-glacier water-amount))))

(defn update-climate [climate base-temp angle]
  (let [water-amount (:water-amount climate)
        season-temp (* base-temp (+ 1 (* 0.1 (Math/sin angle))))]
    (-> climate
        (assoc :base-temp base-temp)
        (assoc :season-temp season-temp)
        (assoc :sea-level (adjusted-sea-level season-temp water-amount)))))