(ns astrogator.generation.body.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.util.log :as log]
            [astrogator.generation.system.lunar :as l]
            [astrogator.generation.body.surface :as surf]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.generation.expandable :as exp]
            [astrogator.physics.move.rotate :as rot]
            [astrogator.poetry.names :as n]
            [astrogator.physics.thermal.climate :as c]))

(defrecord Planet [mass radius seed name rhill orbit climate rotation mappos color circumbinary]
  orb/Orbit (orbit-move [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos))
  rot/Rot (rotate [this dt] (rot/rotate this dt))
  exp/Seed (expand [this]
             (do (log/info "extracting planet: " (:seed this))
                 (r/set-seed! (:seed this))
                 (let [circumbinary false
                       {flux    :flux
                        climate :climate
                        rhill   :rhill
                        mass    :mass} this]
                   (-> this
                       (assoc :descriptors (surf/get-descriptors climate flux circumbinary))
                       (assoc :surface (surf/cellular-map 16 0.45 4 8 0.2))
                       (assoc :moons (l/generate-moon-system mass (* 0.1 rhill) rhill)))))))

(defn generate-planet [parent-mass seed orbit-radius circumbinary]
  (let [mass (r/planetary-imf)
        radius (a/planet-radius mass :Me)
        orbit (orb/circular-orbit parent-mass [orbit-radius nil])
        climate (c/climate 0 (r/uniform 0.05 0.95))
        rotation (rot/rotation (+ (r/uniform) (r/poisson 2)))
        rhill (a/hill-sphere orbit-radius (unit/conv mass :Me :Msol) parent-mass)
        color {:rock    [(r/uniform 0.0 0.25) 0.6 0.6]
               :ocean   [(r/uniform 0.5 0.75) 0.6 0.6]
               :glacier [(r/uniform 0.5 0.75) 0.2 0.8]}
        mappos [0 0]
        name (n/generate-name seed (r/rand-n 5 7))]
    (->Planet mass radius seed name rhill orbit climate rotation mappos color circumbinary)))
