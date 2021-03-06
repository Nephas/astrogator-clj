(ns astrogator.generation.system.distantsystem
  (:require [astrogator.generation.system.system :as sys]
            [astrogator.generation.expandable :as exp]
            [astrogator.util.log :as log]
            [astrogator.physics.astro :as a]
            [astrogator.poetry.names :as n]
            [astrogator.util.rand :as r]
            [astrogator.physics.trafo :as trafo]
            [astrogator.render.draw.body :as draw]
            [astrogator.physics.trafo :as t]
            [astrogator.render.draw.geometry :as geo]
            [astrogator.generation.body.ship :as pl]
            [astrogator.physics.units :as u]))

(defrecord DistantSystem [sectorpos sectorvel seed mass luminosity color magnitude name]
  trafo/Distance
  (dist [this other] (trafo/v-dist (:sectorpos this) (:sectorpos other)))

  exp/Seed
  (same? [this other] (exp/equal-by-seed this other))
  (expand [this]
    (do (log/info "extracting system: " (:seed this))
        (let [system (sys/generate-system (:mass this) (:seed this) true)]
          (-> system
              (sys/initiate-positions)
              (pl/init-npcs))))))

(extend DistantSystem draw/Drawable
  (merge draw/drawable-impl
         {:draw-distant (fn [this camera] (let [pos (t/map-to-screen (:sectorpos this) camera)
                                                size (* -1 (camera :obj-zoom) (:magnitude this))]
                                            (geo/airy pos size (:color this))))}))

(defn generate-distant-system [mass seed sectorpos]
  (let [system (sys/generate-system mass seed false)
        sectorvel (r/rand-polar (u/conv 100 :km/s :AU/d))
        luminosity (sys/get-system-luminosity system)
        color (sys/get-system-color system)
        magnitude (a/get-magnitude luminosity)
        name (n/generate-name seed (r/rand-n 4 6))]
    (->DistantSystem sectorpos sectorvel seed mass luminosity color magnitude name)))
