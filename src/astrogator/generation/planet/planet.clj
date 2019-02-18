(ns astrogator.generation.planet.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.generation.moon :as m]
            [astrogator.generation.planet.surface :as s]
            [astrogator.generation.belt :as b]))

(defn generate-planet [parent-mass orbit-radius]
  (let [mass (r/rand-range 0.5 100)
        radius-Re (a/planet-radius mass :Me)
        torbit (a/t-orbit orbit-radius :AU parent-mass :Msol)
        moon-min-orbit (* 10 (unit/conv radius-Re :Re :AU))
        rhill (a/hill-sphere orbit-radius (unit/conv mass :Me :Msol) parent-mass)]
    {:type    :planet
     :mass    mass
     :radius  radius-Re
     :rhill   rhill
     :torbit  torbit
     :cylvel  (* 2 Math/PI (/ 1 torbit))
     :cylpos  [orbit-radius (* 2 Math/PI (r/rand))]
     :color   [128 196 128]
     :mappos  [0 0]
     :surface {}                                              ;(s/cellular-map 4 0.45 4 8 0.2 0.4 0 0.4)
     :moons   (m/generate-moon-system mass moon-min-orbit rhill)}))

(defn randomize-system-structure [planet-probability n-planets]
  (let [rand-pairs (map (fn [i] (if (< (r/rand) planet-probability) [i nil] [nil i]))
                        (range n-planets))]
    {:planet-indices (filterv #(not= nil %) (map #(first %) rand-pairs))
     :belt-indices   (filterv #(not= nil %) (map #(last %) rand-pairs))}))

(defn generate-planet-system [parent-mass inner-radius outer-radius]
  (let [n-planets (r/rand-int-range 5 20)
        indices (randomize-system-structure 0.8 n-planets)
        planet-radii (filterv #(< % outer-radius)
                              (map #(a/titius-bode % inner-radius)
                                   (indices :planet-indices)))
        belt-radii (filterv #(< % outer-radius)
                            (map #(a/titius-bode % inner-radius)
                                 (indices :belt-indices)))]
    {:planets   (mapv #(generate-planet parent-mass %) planet-radii)
     :particles (apply concat
                       (mapv #(b/generate-asteroid-belt parent-mass %1 %1) belt-radii))}))


