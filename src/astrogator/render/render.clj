(ns astrogator.render.render
  (:require [astrogator.render.system :as sys]
            [astrogator.render.sector :as sec]
            [astrogator.render.conf :as conf]
            [quil.core :as q]
            [astrogator.util.util :as u]))



(defn render-universe [universe camera]
  (q/background (u/vec-to-color conf/back-color))
  (if (= :system (camera :scale))
    (sys/draw-system (universe :viewsystem) camera)
    (sec/draw-sector (universe :sector) (universe :clouds) camera)))