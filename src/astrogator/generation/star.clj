(ns astrogator.generation.star
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.generation.system.planetary :as ps]))

(defrecord Star [type mass radius rhill luminosity temp class color])

(defn generate-star [mass max-sc-orbit planets?]
  (let [radius (a/mass-radius mass)
        min-sc-orbit (* 100 (unit/conv radius :Rsol :AU))
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :Lsol radius :Rsol)
        class (a/spectral-class temp)
        color (a/COLOR class)]
    (conj {:body (->Star :star mass radius max-sc-orbit luminosity temp class color)}
          (if planets? (ps/generate-planet-system mass min-sc-orbit max-sc-orbit)))))