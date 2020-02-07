(ns astrogator.generation.body.asteroid
  (:require [astrogator.physics.move.orbit :as orb]
            [astrogator.physics.move.orbit :as o]
            [astrogator.render.draw.body :as draw]))

(defrecord Asteroid [mappos])

(extend Asteroid draw/Drawable draw/drawable-impl)
(extend Asteroid orb/Orbit orb/orbit-impl)

(defn generate-asteroid [parent-mass orbit-radius]
  (let [mappos [0 0]]
    (-> (->Asteroid mappos)
        (o/init-orbit [parent-mass :Msol] [orbit-radius nil] nil))))

