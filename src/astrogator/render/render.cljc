(ns astrogator.render.render
  (:require [astrogator.render.system :as sys]
            [quil.core :as q]
            [astrogator.util.log :as log]
            [astrogator.render.sector :as sec]
            [astrogator.render.conf :as conf]))

(defn render-universe [universe camera]
  (apply q/background conf/back-color)
  (case (camera :scale)
    :body (sys/draw-refbody (universe :refsystem) camera)
    :subsystem (sys/draw-system (universe :refsystem) camera)
    :system (sys/draw-system (universe :refsystem) camera)
    :sector (sec/draw-sector (universe :sector) (universe :clouds) camera)))

(defn cache-all [universe camera]
  (do (log/info "caching refsystem")
      (sys/draw-system (universe :refsystem) camera)
      (log/info "caching sector")
      (sec/draw-sector (universe :sector) (universe :clouds) camera)))