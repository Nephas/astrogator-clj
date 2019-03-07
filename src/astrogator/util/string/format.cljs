(ns astrogator.util.string.format
  (:require [cljs.pprint :as pprint]))

(defn f-str [fmt & args] (apply pprint/cl-format nil fmt args))