(ns astrogator.physics.trail
  (:require [astrogator.physics.trafo :as trafo]
            [astrogator.render.draw.geometry :as geo]
            [astrogator.util.color :as col]))

(def trail-length 100)

(defn enum [s]
  (map vector (range) s))

(defprotocol Trail
  (extend [this t] "Update map-positions in the Trail list"))

(defn update-trail [body dt]
  (update-in body [:trail :pos] #(take trail-length (cons (:mappos body) %))))

(defn draw-trail [body camera color]
  (let [alpha-max 128
        alpha (fn [i] (- alpha-max (* (/ alpha-max trail-length) i)))]
    (doseq [[i point] (enum (get-in body [:trail :pos]))]
      (geo/particle (trafo/map-to-screen point camera) (col/with-alpha color (alpha i))))))