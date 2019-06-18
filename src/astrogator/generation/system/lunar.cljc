(ns astrogator.generation.system.lunar
  (:require [astrogator.generation.body.moon :as m]
            [astrogator.util.rand :as r]
            [astrogator.physics.astro :as a]))

(defn generate-moon-system [parent-mass inner-radius outer-radius]
  (let [moon-limit (int (+ 2 (Math/sqrt parent-mass)))
        n-moons (r/rand-n moon-limit)
        radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius)
                            (range n-moons)))]
    (mapv #(m/generate-moon parent-mass %) radii)))
