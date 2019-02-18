(ns astrogator.render.render
  (:require [astrogator.render.system :as sys]
            [quil.core :as q]
            [astrogator.render.sector :as sec]
            [astrogator.render.conf :as conf]))

(defn render-universe [universe camera]
  (apply q/background conf/back-color)
  (case (camera :scale)
    :body (sys/draw-refbody (universe :viewsystem) camera)
    :subsystem (sys/draw-system (universe :viewsystem) camera)
    :system (sys/draw-system (universe :viewsystem) camera)
    :sector (sec/draw-sector (universe :sector) (universe :clouds) camera)))

(defn cache-all [universe camera]
  (do (sys/draw-system (universe :viewsystem) camera)
      (sec/draw-sector (universe :sector) (universe :clouds) camera)))