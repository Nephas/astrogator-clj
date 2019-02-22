(ns astrogator.generation.system.planetary
  (:require [astrogator.util.rand :as r]
            [astrogator.physics.astro :as a]
            [astrogator.generation.asteroid :as b]
            [astrogator.generation.moon :as m]
            [astrogator.generation.planet.planet :as p]))

(defn randomize-system-structure [planet-probability n-planets]
  (let [rand-pairs (map (fn [i] (if (< (r/rand) planet-probability) [i nil] [nil i]))
                        (range n-planets))]
    {:planet-indices (filterv #(not= nil %) (map #(first %) rand-pairs))
     :belt-indices   (filterv #(not= nil %) (map #(last %) rand-pairs))}))

(defn generate-asteroid-belt [parent-mass inner-radius outer-radius]
  (let [n-particles (r/rand-int-range 25 50)
        radii (map (fn [n] (r/rand-range (* 0.95 inner-radius) (* 1.05 outer-radius)))
                   (range n-particles))]
    (mapv #(b/generate-asteroid parent-mass %) radii)))

(defn generate-planet-system [parent-mass inner-radius outer-radius]
  (let [n-planets (r/rand-int-range 5 20)
        indices (randomize-system-structure 0.8 n-planets)
        planet-radii (filterv #(< % outer-radius)
                              (map #(a/titius-bode % inner-radius)
                                   (indices :planet-indices)))
        belt-radii (filterv #(< % outer-radius)
                            (map #(a/titius-bode % inner-radius)
                                 (indices :belt-indices)))]
    {:planets   (mapv #(p/generate-planet parent-mass (r/new-seed) %) planet-radii)
     :asteroids (apply concat
                       (mapv #(generate-asteroid-belt parent-mass %1 %1) belt-radii))}))
