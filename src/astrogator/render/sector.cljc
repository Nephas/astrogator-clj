(ns astrogator.render.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]
            [astrogator.render.body.body :as b]
            [astrogator.gui.camera :as c]
            [astrogator.state.selectors :as s]
            [quil.core :as q]
            [astrogator.render.conf :as conf]
            [astrogator.util.color :as col]))

(defn get-playership-sectorpos [state]
  (t/add (:mappos (s/get-playership state)) (:sectorpos (s/get-refsystem state))))


(defn draw-cloud
  ([pos size color zoom]
   (let [mag (/ (- (conf/thresholds :system) zoom) (conf/thresholds :system))
         mag (* mag)]
     (col/fill color (* mag 192))
     (q/with-stroke [(apply q/color color) (* mag 128)]
                    (do (q/stroke-weight (* size 2))
                        (geo/circle pos size))))))

(defn draw-sector [systems clouds camera]
  (do (doseq [cloud clouds]
        (let [pos (t/map-to-screen (cloud :sectorpos) camera)
              size (* (cloud :radius) (camera :dist-zoom))]
          (draw-cloud pos size (cloud :color) (camera :dist-zoom))))
      (let [on-screen (fn [distantsystem] (c/on-screen? (t/map-to-screen (:sectorpos distantsystem) camera)))
            visible-systems (take 1000 (filter on-screen systems))]
        (doseq [distantsystem visible-systems]
          (let [pos (t/map-to-screen (:sectorpos distantsystem) camera)
                size (* -1 (camera :obj-zoom) (:magnitude distantsystem))]
            (geo/airy pos size (:color distantsystem)))))))