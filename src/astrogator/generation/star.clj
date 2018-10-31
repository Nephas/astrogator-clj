(ns astrogator.generation.star
  (:require [quil.core :as q]
            [astrogator.physics.astro :as a]
            [astrogator.generation.planet :as p]
            [astrogator.util.util :as u]))

(defn generate-star [mass max-sc-orbit]
  (let [radius (a/mass-radius mass)
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :L* radius :R*)
        class (a/spectral-class temp)
        color (a/COLOR class)]
    {:body    {:type       :star
               :mass       mass
               :radius     radius
               :luminosity luminosity
               :temp       temp
               :class      class
               :color      (u/vec-to-color color)}
     :planets (p/generate-planet-system mass (* 0.1 max-sc-orbit) (* 0.9 max-sc-orbit))}))