(ns astrogator.render.sector
  (:require [astrogator.physics.trafo :as t]
            [astrogator.conf :as c]
            [quil.core :as q]))


(defn draw-sector [systems clouds camera]
  (do (doseq [cloud clouds]
        (let [pos (t/map-to-screen (cloud :sectorpos) camera)
              size (* (cloud :radius) (camera :dist-zoom))]
          (do (q/fill (cloud :color))
              (q/ellipse (pos 0) (pos 1) size size))))
      (doseq [system systems]
        (let [pos (t/map-to-screen (system :sectorpos) camera)
              size (* (camera :obj-zoom) (system :magnitude))]
          (do (q/fill (system :color))
              (q/ellipse (pos 0) (pos 1) size size))))))