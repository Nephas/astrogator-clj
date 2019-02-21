(ns astrogator.generation.star
  (:require [astrogator.physics.astro :as a]
            [astrogator.generation.planet.planet :as p]
            [astrogator.physics.units :as unit]
            [astrogator.generation.belt :as b]))

(defn generate-star [mass max-sc-orbit planets?]
  (let [radius (a/mass-radius mass)
        min-sc-orbit (* 100 (unit/conv radius :Rsol :AU))
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :Lsol radius :Rsol)
        class (a/spectral-class temp)
        color (a/COLOR class)]
    (conj {:body {:type       :star
                  :mass       mass
                  :radius     radius
                  :luminosity luminosity
                  :temp       temp
                  :class      class
                  :color      color
                  :rhill      max-sc-orbit}}
          (if planets? (p/generate-planet-system mass min-sc-orbit max-sc-orbit)))))