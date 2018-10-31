(ns astrogator.generation.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.generation.moon :as m]
            [quil.core :as q]
            [astrogator.util.util :as u]))

(defn generate-planet [parent-mass orbit-radius]
  (let [mass (r/rand-range 0.5 100)
        radius-Re (a/planet-radius mass :Me)
        torbit (a/t-orbit orbit-radius :AU parent-mass :M*)
        moon-min-orbit (* 10 (unit/conv radius-Re :Re :AU))]
    {:type   :planet
     :mass   mass
     :radius radius-Re
     :torbit torbit,
     :cylvel (* 2 Math/PI (/ 1 torbit)),
     :cylpos [orbit-radius (* 2 Math/PI (r/rand))]
     :color  (u/vec-to-color [128 196 128])
     :mappos [0 0]
     :moons  (m/generate-moon-system mass moon-min-orbit (* 100 moon-min-orbit))}))

(defn generate-planet-system [parent-mass inner-radius outer-radius]
  (let [n-planets (r/rand-int-range 5 10)
        radii (filterv #(< % outer-radius)
                       (map #(a/titius-bode % inner-radius)
                            (range n-planets)))]
    (mapv #(generate-planet parent-mass %) radii)))