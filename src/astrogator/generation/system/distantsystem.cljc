(ns astrogator.generation.system.distantsystem
  (:require [astrogator.generation.system.system :as sys]
            [astrogator.generation.expandable :as exp]
            [astrogator.util.log :as log]
            [astrogator.physics.astro :as a]
            [astrogator.poetry.names :as n]
            [astrogator.util.rand :as r]))

(defrecord DistantSystem [sectorpos seed mass luminosity color magnitude name]
  exp/Seed (same? [this other] (exp/equal-by-seed this other))
  (expand [this]
             (do (log/info "extracting system: " (:seed this))
                 (let [system (sys/generate-system (:mass this) (:seed this) true)]
                   (-> system
                       (sys/initiate-positions)
                       (assoc :ships [] ))))))

(defn generate-distant-system [mass seed sectorpos]
  (let [system (sys/generate-system mass seed false)
        luminosity (sys/get-system-luminosity system)
        color (sys/get-system-color system)
        magnitude (a/get-magnitude luminosity)
        name (n/generate-name seed (r/rand-n 4 6))]
    (->DistantSystem sectorpos seed mass luminosity color magnitude name)))
