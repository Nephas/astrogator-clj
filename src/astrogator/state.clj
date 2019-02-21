(ns astrogator.state
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.gui.sector :as sec]))

(defn init-state []
  (let [state {:universe  {:viewsystem nil
                           :sector     (gensec/generate-sector 50 20000)
                           :clouds     (gensec/generate-clouds 50 50)}
               :camera    {:mouse     {:screenpos [0 0]
                                       :mappos    [0 0]}
                           :dist-zoom 5
                           :obj-zoom  1
                           :sectorpos [0 0]
                           :mappos    [0 0]
                           :scale     :system
                           :refbody   nil}
               :animation {:target 0}
               :time      {:day 0
                           :dps 10}}]
    (-> state
        (sec/change-viewsystem
          (first (get-in state [:universe :sector]))))))