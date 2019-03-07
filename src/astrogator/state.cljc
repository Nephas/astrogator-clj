(ns astrogator.state
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.gui.sector :as sec]
            [astrogator.physics.move.system :as p]
            [astrogator.util.log :as log]
            [astrogator.render.render :as render]
            [astrogator.render.gui.gui :as gui]
            [astrogator.util.rand :as rand]))

(def init-state
  {:universe  {:reset      false
               :refsystem nil
               :sector     []
               :clouds     []}
   :camera    {:mouse        {:screenpos [0 0]
                              :mappos    [0 0]}
               :dist-zoom    5
               :obj-zoom     1
               :sectorpos    [0 0]
               :mappos       [0 0]
               :scale        :system
               :refbody      nil
               :targetbody   nil
               :refsystem    nil
               :targetsystem nil}
   :animation {:target 0
               :load   5}
   :time      {:day 0
               :dps 1}})

(defn generate-universe [state]
  (let [game-state (-> state (assoc-in [:universe :sector] (gensec/generate-sector 50 10000))
                       (assoc-in [:universe :clouds] (gensec/generate-clouds 50 25)))]
    (-> game-state
        (sec/change-refsystem (rand/rand-coll (get-in game-state [:universe :sector])))
        (p/move-refsystem))))

(defn load-universe [store screen]
  (do (gui/loading-screen screen)
      (log/info "initialising state")
      (let [game-state (generate-universe init-state)]
        (do (log/info "caching renderings")
            (render/cache-all (game-state :universe) (game-state :camera))
            (log/info "setting state atom")
            (reset! store game-state)))))