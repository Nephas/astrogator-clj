(ns astrogator.generation.system.distantsystem
  (:require [astrogator.generation.system.system :as sys]
            [astrogator.generation.expandable :as exp]
            [astrogator.physics.astro :as a]))

(defrecord DistantSystem [sectorpos seed mass luminosity color magnitude]
  exp/Seed (expand [this] (sys/initiate-positions
                        (sys/generate-system (:mass this) (:seed this) true))))

(defn generate-distant-system [mass seed sectorpos]
  (let [system (sys/generate-system mass seed false)
        luminosity (sys/get-system-luminosity system)
        color (sys/get-system-color system)
        magnitude (a/get-magnitude luminosity)]
    (->DistantSystem sectorpos seed mass luminosity color magnitude)))
