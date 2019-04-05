(ns astrogator.render.body.body
  (:require [quil.core :as q]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]))

(defn cloud
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (col/fill color (* mag 192))
     (q/with-stroke [(apply q/color color) (* mag 128)]
                    (do (q/stroke-weight (* size 2))
                        (geo/circle pos size))))))

(defn distant-planet
  ([pos size phase color]
   (if (< size conf/airy-threshold)
     (geo/airy pos 1 color)
     (geo/half-circle pos size phase color))))

(defn distant-star
  ([pos size color]
   (col/fill color)
   (if (< size conf/airy-threshold)
     (geo/airy pos 2 color)
     (do (col/fill color)
         (q/with-stroke [(apply q/color (assoc color 2 0.66)) 256]
                        (do (q/stroke-weight (* size 0.2))
                            (geo/circle pos size)))))))
