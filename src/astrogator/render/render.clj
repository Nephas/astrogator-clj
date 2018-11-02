(ns astrogator.render.render
  (:require [astrogator.render.system :as sys]
            [astrogator.render.sector :as sec]
            [astrogator.render.conf :as conf]
            [quil.core :as q]
            [astrogator.util.color :as col]))



(defn render-universe [universe camera]
  (q/background (col/vec-to-color conf/back-color))
  (case (camera :scale)
    :body (sys/draw-refbody (universe :viewsystem) camera)
    :subsystem (sys/draw-subsystems (universe :viewsystem) camera)
    :system (sys/draw-system (universe :viewsystem) camera)
    :sector (sec/draw-sector (universe :sector) (universe :clouds) camera)))