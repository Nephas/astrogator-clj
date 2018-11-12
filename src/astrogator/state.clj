(ns astrogator.state
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.gui.sector :as sec]))

(defn init-state []
  (let [state {:universe {:viewsystem nil
                          :sector     (gensec/generate-sector 5 2000)
                          :clouds     (gensec/generate-clouds 5 50)}
               :camera   {:dist-zoom 5
                          :obj-zoom  1
                          :sectorpos [0 0]
                          :mappos    [0 0]
                          :scale     :system
                          :refbody   nil}
               :time     {:dps 10}}]
    (-> state
        (sec/change-viewsystem
          (first (get-in state [:universe :sector]))))))