(ns astrogator.generation.star
  (:require [quil.core :as q]
            [astrogator.physics.astro :as a]
            [astrogator.generation.planet :as p]))

(defn generate-star [mass max-sc-orbit]
  (let [radius (a/mass-radius mass)
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann-temp luminosity radius)
        class (a/spectral-class temp)
        color (a/COLOR class)]
    {:star    {:mass       mass
               :radius     radius
               :luminosity luminosity
               :temp       temp
               :class      class
               :color      (q/color (color 0) (color 1) (color 2))}
     :planets (p/generate-planet-system mass (* 0.1 max-sc-orbit) (* 0.9 max-sc-orbit))}))