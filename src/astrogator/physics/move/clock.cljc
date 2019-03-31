(ns astrogator.physics.move.clock
  (:require [astrogator.util.math :as m]))

(defprotocol Tick
  (tick [this dt] [this dt beta]))

(defrecord Clock [day year dps] Tick
  (tick [this dt] (let [day (float (+ (:day this) dt))
                             year (int (/ day 365.25))]
                         (-> this
                             (assoc :day day)
                             (assoc :year year))))
  (tick [this dt beta] (let [gamma (Math/sqrt (float (- 1.0 (* beta beta))))]
                                 (tick this (* gamma dt)))))

(defn clock [] (->Clock 0 0 1))