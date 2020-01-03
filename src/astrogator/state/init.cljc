(ns astrogator.state.init
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.physics.move.clock :as c]
            [astrogator.physics.move.system :as p]
            [astrogator.util.log :as log]
            [astrogator.render.universe :as render]
            [astrogator.util.rand :as rand]
            [astrogator.state.global :as g]
            [astrogator.gui.camera :as cam]
            [astrogator.generation.body.ship :as pl]
            [astrogator.gui.message :as m]))

(def init-state
  {:universe  {:refsystem nil
               :sector    []
               :clouds    []}
   :camera    {:mouse        {:screenpos [0 0]
                              :mappos    [0 0]}
               :dist-zoom    5
               :obj-zoom     1
               :acc-zoom     0
               :sectorpos    [0 0]
               :mappos       [0 0]
               :scale        :system
               :refbody      nil
               :targetbody   nil
               :refsystem    nil
               :targetsystem nil}
   :message   {:window (into [] (reverse m/start-messages))
               :log    []}
   :animation {:target 0
               :load   5}
   :time      nil})

(defn generate-universe [state]
  (let [sector (gensec/generate-sector 50 10000)
        clouds (gensec/generate-clouds 50 25)
        refsystem (rand/rand-coll sector)]
    (-> state
        (assoc-in [:universe :sector] sector)
        (assoc-in [:universe :clouds] clouds)
        (assoc :time (c/clock))
        (cam/change-refsystem refsystem)
        (pl/init-playership)
        (p/move-universe))))

(defn init-universe [_]
  (do (log/info "initialising state")
      (let [game-state (generate-universe init-state)]
        (do (log/info "caching renderings")
            (render/cache-all (game-state :universe) (game-state :camera))
            (log/info "setting state atom")
            (reset! g/store game-state)))))