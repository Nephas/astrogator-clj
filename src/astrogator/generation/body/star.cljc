(ns astrogator.generation.body.star
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.generation.system.planetary :as ps]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.poetry.names :as n]
            [astrogator.util.rand :as r]))

(defrecord Star [mass radius rhill luminosity temp class color name]
  orb/Orbit (orbit-move [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos)))

(defn generate-star [mass max-sc-orbit planets?]
  (let [radius (a/mass-radius mass)
        min-sc-orbit (* 10 (unit/conv radius :Rsol :AU))
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :Lsol radius :Rsol)
        class (a/spectral-class temp)
        color (a/COLOR class)
        name (n/generate-name (r/rand-n 3 4))]
    (conj {:body (->Star mass radius max-sc-orbit luminosity temp class color name)}
          (if planets? (ps/generate-planet-system mass min-sc-orbit max-sc-orbit false)))))