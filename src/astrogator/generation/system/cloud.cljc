(ns astrogator.generation.system.cloud
  (:require [astrogator.render.draw.body :as draw]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.render.draw.geometry :as geo]))

(defrecord Cloud [radius sectorpos color])

(extend Cloud draw/Drawable
  (merge draw/drawable-impl
         {:draw-distant (fn [this camera]
                          (let [pos (t/map-to-screen (:sectorpos this) camera)
                                size (* (:radius this) (:dist-zoom camera))
                                color (:color this)
                                zoom (:dist-zoom camera)
                                mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))]
                            (col/fill color (* mag 192))
                            (q/with-stroke [(apply q/color color) (* mag 128)]
                                           (do (q/stroke-weight (* size 2))
                                               (geo/circle pos size)))))}))

(defn generate-cloud [radius sectorpos color]
  (->Cloud radius sectorpos color))
