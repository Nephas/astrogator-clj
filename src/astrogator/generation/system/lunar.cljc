(ns astrogator.generation.system.lunar
  (:require [astrogator.generation.moon :as m]
            [astrogator.util.rand :as r]
            [astrogator.physics.astro :as a]))

(defn generate-moon-system [parent-mass inner-radius outer-radius]
  (let [n-moons (r/rand-n 0 10)
        radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius)
                            (range n-moons)))]
    (mapv #(m/generate-moon parent-mass %) radii)))
