(ns astrogator.render.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]
            [astrogator.render.body :as b]
            [astrogator.gui.camera :as c]))


(defn draw-sector [systems clouds camera]
  (do (doseq [cloud clouds]
        (let [pos (t/map-to-screen (cloud :sectorpos) camera)
              size (* (cloud :radius) (camera :dist-zoom))]
          (b/cloud pos size (cloud :color) (camera :dist-zoom))))
      (let [on-screen (fn [distantsystem] (c/on-screen? (t/map-to-screen (:sectorpos distantsystem) camera)))
            visible-systems (take 1000 (filter on-screen systems))]
        (doseq [distantsystem visible-systems]
          (let [pos (t/map-to-screen (:sectorpos distantsystem) camera)
                size (* -1 (camera :obj-zoom) (:magnitude distantsystem))]
            (geo/airy pos size (:color distantsystem)))))))