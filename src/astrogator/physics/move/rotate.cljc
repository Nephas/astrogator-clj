(ns astrogator.physics.move.rotate
  (:require [astrogator.util.rand :as r]))

(defrecord Rotation [angle angvel])

(defprotocol Rot
  (init-at [this angvel] "initiate a rotation variables")
  (rotate-step [this dt] "increase rotation angle at fixed velocity for dt"))

(def rot-impl {:init-at (fn [this angvel] (assoc this :rotation (->Rotation (r/phase) angvel)))
               :rotate-step (fn [this dt]
                              (let [{angle  :angle
                                     angvel :angvel} (:rotation this)]
                                (assoc-in this [:rotation :angle] (mod (+ angle (* dt angvel)) (* 2 Math/PI)))))})