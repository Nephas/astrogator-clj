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
            [astrogator.physics.trafo :as trafo]
            [astrogator.render.draw.body :as draw]
            [astrogator.physics.trafo :as t]
            [astrogator.util.color :as col]
            [astrogator.render.conf :as conf]
            [astrogator.render.draw.geometry :as geo]
            [quil.core :as q]
            [astrogator.render.tilemap :as tm]))

(defrecord Star [mass radius rhill luminosity temp class color name]

  exp/Seed
  (same? [this other] (exp/equal-by-seed this other))
  (expand [this]
    (do (log/info "extracting star: " (:name this))
        (let [phase-seed (fn [tile] (assoc tile :seed (r/phase)))
              tile-map (m/init-tiles m/star-tile 32)]
          (assoc this :surface (-> tile-map
                                   (m/init-map)
                                   (u/update-values phase-seed)))))))

(extend Star orb/Orbit orb/orbit-impl)
(extend Star trafo/Distance trafo/distance-impl)
(extend Star rot/Rot rot/rot-impl)
(extend Star draw/Drawable
  (merge draw/drawable-impl
         {:draw-distant (fn [this camera]
                          (let [pos (t/map-to-screen (:mappos this) camera)
                                size (* 5 (camera :obj-zoom) (:radius this))
                                color (:color this)]
                            (col/fill color)
                            (if (< size conf/airy-threshold)
                              (geo/airy pos 2 color)
                              (do (col/fill color)
                                  (q/with-stroke [(apply q/color (assoc color 2 0.66)) 256]
                                                 (do (q/stroke-weight (* size 0.2))
                                                     (geo/circle pos size)))))))

          :draw-surface (fn [this camera]
                          (q/stroke-weight 1)
                          (let [scale (* 0.25 (camera :obj-zoom) (:radius this))]
                            (tm/draw-tilemap this scale)))

          :draw-detail  (fn [this camera]
                          (let [pos (t/map-to-screen (:mappos this) camera)
                                size (* 5 (camera :obj-zoom) (:radius this))
                                color (draw/main-color this)]
                            (do (col/fill color)
                                (do (draw/draw-surface this camera)
                                    (geo/ring pos (* 1.6 size) (assoc color 2 0.66) (* 0.2 size))))))}))

(defn generate-star [mass max-sc-orbit planets?]
  (let [radius (a/mass-radius mass)
        min-sc-orbit (* 10 (unit/conv radius :Rsol :AU))
        luminosity (a/mass-luminosity mass)
        temp (a/stefan-boltzmann luminosity :Lsol radius :Rsol)
        class (a/spectral-class temp)
        color (a/COLOR class)
        name (n/generate-name (r/rand-n 3 4))]
    (conj {:body (-> (->Star mass radius max-sc-orbit luminosity temp class color name)
                     (rot/init-at (r/uniform 0.01 0.5)))}
          (if planets? (ps/generate-planet-system mass min-sc-orbit max-sc-orbit false)))))