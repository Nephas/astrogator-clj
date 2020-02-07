(ns astrogator.physics.trail
  (:require [astrogator.physics.trafo :as trafo]
            [astrogator.render.draw.geometry :as geo]
            [astrogator.util.color :as col]))

(def trail-length 100)

(defn enum [s]
  (map vector (range) s))

(defprotocol Trail
  (update-step [this t] "Update map-positions in the Trail list"))

(def trail-impl {:update-step (fn [this t] (update-in this [:trail :pos] #(take trail-length (cons (:mappos this) %))))})

(defn draw-trail [body camera color]
  (let [alpha-max 128
        alpha (fn [i] (- alpha-max (* (/ alpha-max trail-length) i)))]
    (doseq [[i point] (enum (get-in body [:trail :pos]))]
      (geo/particle (trafo/map-to-screen point camera) (col/with-alpha color (alpha i))))))