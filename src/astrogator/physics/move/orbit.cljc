(ns astrogator.physics.move.orbit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]))

(defrecord Orbit-Elements [cylvel cylpos torbit parent])

(defprotocol Orbit "an object in circular orbit around a parent"
  (init-orbit [this [parent-mass unit] [radius phi] parent-path] "initiate orbital elements for a parent body")
  (orbit-move [this dt parent-mappos] "evolve the orbital elements by dt"))

(def orbit-impl
  {:init-orbit (fn [this [parent-mass unit] [radius phi] parent-path]
                 (let [phi (if (nil? phi) (* 2 Math/PI (r/uniform)) phi)
                       torbit (a/t-orbit radius :AU parent-mass unit)
                       cylvel (* 2 Math/PI (/ 1 torbit))]
                   (assoc this :orbit (->Orbit-Elements cylvel [radius phi] torbit parent-path))))

   :orbit-move (fn [this dt parent-mappos] (let [{[radius phase] :cylpos
                                                  cylvel         :cylvel} (:orbit this)
                                                 new-phase (+ phase (* dt cylvel))
                                                 new-cylpos [radius new-phase]
                                                 mappos (t/add parent-mappos (t/pol-to-cart new-cylpos))]
                                             (-> this
                                                 (assoc-in [:mappos] mappos)
                                                 (assoc-in [:orbit :cylpos] new-cylpos))))})

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))