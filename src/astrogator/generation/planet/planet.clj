(ns astrogator.generation.planet.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.generation.moon :as m]
            [astrogator.generation.planet.surface :as surf]
            [astrogator.util.rand :as rand]
            [astrogator.generation.expandable :as exp]))

;TODO move planetary generation pars to planet
(defrecord Planet [type mass radius seed rhill torbit cylvel cylpos mappos color]
  exp/Seed (expand [this]
         (do (rand/set-seed! (:seed this))
             (-> this
                 (assoc :surface (surf/cellular-map 16 0.45 4 8 0.2 0.4 0 0.4))
                 (assoc :moons (m/generate-moon-system (:mass this) (* 0.01 (:rhill this)) (:rhill this)))))))

(defn generate-planet [parent-mass seed orbit-radius]
  (let [mass (r/rand-range 0.5 100)
        radius (a/planet-radius mass :Me)
        torbit (a/t-orbit orbit-radius :AU parent-mass :Msol)
        rhill (a/hill-sphere orbit-radius (unit/conv mass :Me :Msol) parent-mass)
        color {:rock    [(r/rand-range 0.0 0.25) 0.6 0.6]
               :ocean   [(r/rand-range 0.5 0.75) 0.6 0.6]
               :glacier [(r/rand-range 0.5 0.75) 0.2 0.8]}
        cylvel (* 2 Math/PI (/ 1 torbit))
        cylpos [orbit-radius (* 2 Math/PI (r/rand))]
        mappos [0 0]]
    (->Planet :planet mass radius seed rhill torbit cylvel cylpos mappos color)))
