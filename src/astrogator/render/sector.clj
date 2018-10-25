(ns astrogator.render.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]))


(defn draw-sector [systems clouds camera]
  (do (doseq [cloud clouds]
        (let [pos (t/map-to-screen (cloud :sectorpos) camera)
              size (* (cloud :radius) (camera :dist-zoom))]
          (geo/cloud pos size (cloud :color))))
      (doseq [system systems]
        (let [pos (t/map-to-screen (system :sectorpos) camera)
              size (* -1 (camera :obj-zoom) (system :magnitude))]
          (geo/airy pos size (system :color))))))