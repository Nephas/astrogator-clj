(ns astrogator.physics.move.rotate
  (:require [astrogator.util.rand :as r]))

(defprotocol Rot (rotate [this dt]))

(defrecord Rotation [angle angvel])

(defn rotation
  ([angle angvel] (->Rotation angle angvel))
  ([angvel] (rotation (r/phase) angvel)))

(defn rotate [body dt]
  (let [{angle  :angle
         angvel :angvel} (:rotation body)]
    (assoc-in body [:rotation :angle] (mod (+ angle (* dt angvel)) (* 2 Math/PI)))))
