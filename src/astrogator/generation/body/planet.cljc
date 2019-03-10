(ns astrogator.generation.body.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.util.log :as log]
            [astrogator.generation.system.lunar :as l]
            [astrogator.generation.body.surface :as surf]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.generation.expandable :as exp]
            [astrogator.physics.move.orbit :as o]
            [astrogator.poetry.names :as n]))

;TODO move planetary generation pars to planet
(defrecord Planet [mass radius seed name rhill orbit mappos color circumbinary]
  orb/Orbit (orbit [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos))
  exp/Seed (expand [this]
             (do (log/info (str "extracting planet: " (:seed this)))
                 (r/set-seed! (:seed this))
                 (let [base-temp (:temp this)
                       base-flux (:flux this)
                       circumbinary false
                       water-amount (r/uniform 0.05 0.95)]
                   (-> this
                       (assoc :descriptors (surf/get-descriptors water-amount base-temp base-flux circumbinary))
                       (assoc :surface (surf/cellular-map 16 0.45 4 8 0.2 water-amount base-temp))
                       (assoc :moons (l/generate-moon-system (:mass this) (* 0.1 (:rhill this)) (:rhill this))))))))

(defn generate-planet [parent-mass seed orbit-radius circumbinary]
  (let [mass (r/planetary-imf)
        radius (a/planet-radius mass :Me)
        orbit (o/circular-orbit parent-mass [orbit-radius nil])
        rhill (a/hill-sphere orbit-radius (unit/conv mass :Me :Msol) parent-mass)
        color {:rock    [(r/uniform 0.0 0.25) 0.6 0.6]
               :ocean   [(r/uniform 0.5 0.75) 0.6 0.6]
               :glacier [(r/uniform 0.5 0.75) 0.2 0.8]}
        mappos [0 0]
        name (n/generate-name seed (r/rand-n 5 7))]
    (->Planet mass radius seed name rhill orbit mappos color circumbinary)))
