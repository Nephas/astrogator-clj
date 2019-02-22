(ns astrogator.generation.distantsystem
  (:require [astrogator.generation.system :as sys]
            [astrogator.physics.astro :as a]))

(defprotocol Seed "This is an abstract representation of an Object which can be expanded using 'expand'."
  (expand [this]))

(defrecord DistantSystem [sectorpos seed mass luminosity color magnitude]
  Seed (expand [this] (sys/initiate-positions
                        (sys/generate-system (:mass this) (:seed this) true))))

(defn generate-distant-system [mass seed sectorpos]
  (let [system (sys/generate-system mass seed false)
        luminosity (sys/get-system-luminosity system)
        color (sys/get-system-color system)
        magnitude (a/get-magnitude luminosity)]
    (->DistantSystem sectorpos seed mass luminosity color magnitude)))
