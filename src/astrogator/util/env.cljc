(ns astrogator.util.env
  (:require [quil.core :as q]))

(defn screen-size []
  [(q/width) (q/height)])

(defn screen-center []
  [(* 0.5 (q/width)) (* 0.5 (q/height))])