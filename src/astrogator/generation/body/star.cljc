(ns astrogator.generation.body.star
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.generation.system.planetary :as ps]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.poetry.names :as n]
            [astrogator.util.rand :as r]
            [astrogator.generation.body.tilemap :as m]
            [astrogator.generation.expandable :as exp]
            [astrogator.util.log :as log]
            [astrogator.util.util :as u]
            [astrogator.physics.move.rotate :as rot]
            [astrogator.physics.trafo :as trafo]))

(defrecord Star [mass radius rhill rotation luminosity temp class color name]
  trafo/Distance (dist [this other] (trafo/v-dist (:mappos this) (:mappos other)))
  orb/Orbit (orbit-move [this dt parent-mappos] (orb/move-around-parent this dt parent-mappos))
  rot/Rot (rotate [this dt] (rot/rotate this dt))
  exp/Seed (same? [this other] (exp/equal-by-seed this other))
  (expand [this]
    (do (log/info "extracting star: " (:name this))
        (let [phase-seed (fn [tile] (assoc tile :seed (r/phase)))
              tile-map (m/init-tiles m/star-tile 32)]
          (assoc this :surface (-> tile-map
                                   (m/init-map)
                                   (u/update-values phase-seed)))))))

(defn generate-star [mass max-sc-orbit planets?]
  (let [radius (a/mass-radius mass)
        min-sc-orbit (* 10 (unit/conv radius :Rsol :AU))
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :Lsol radius :Rsol)
        class (a/spectral-class temp)
        color (a/COLOR class)
        name (n/generate-name (r/rand-n 3 4))
        rotation (rot/rotation (r/uniform 0.01 0.5))]
    (conj {:body (->Star mass radius max-sc-orbit rotation luminosity temp class color name)}
          (if planets? (ps/generate-planet-system mass min-sc-orbit max-sc-orbit false)))))