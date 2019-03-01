(ns astrogator.state
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.gui.sector :as sec]
            [astrogator.physics.move.system :as p]
            [astrogator.util.log :as log]
            [astrogator.render.render :as render]
            [astrogator.render.gui :as gui]
            [astrogator.util.rand :as rand]))

(defn init-state []
  (let [state {:universe  {:reset      false
                           :viewsystem nil
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
               :animation {:target 0
                           :load   5}
               :time      {:day 0
                           :dps 10}}]
    (-> state
        (sec/change-viewsystem (rand/rand-coll (get-in state [:universe :sector])))
        (p/move-viewsystem))))

(defn load-universe [store screen]
  (do (gui/loading-screen screen)
      (log/info "initialising state")
      (let [init-state (init-state)]
        (do (log/info "caching renderings")
            (render/cache-all (init-state :universe) (init-state :camera))
            (log/info "setting state atom")
            (reset! store init-state)))))