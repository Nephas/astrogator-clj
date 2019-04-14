(ns astrogator.state.init
  (:require [astrogator.generation.sector :as gensec]
            [astrogator.gui.sector :as sec]
            [astrogator.physics.move.clock :as c]
            [astrogator.physics.move.system :as p]
            [astrogator.util.log :as log]
            [astrogator.render.render :as render]
            [astrogator.util.rand :as rand]
            [astrogator.state.global :as g]))

(def start-messages ["You slowly drift back into con~scious~ness, cold metal and dark~ness enclo~sing you from all sides. The surging panic blocks any rea~sonable thought and you take an eternity to remem~ber..."
                     "...You're inside a Cryo-~Sarco~phague. Hastily you feel around for the emer~gency release, and bash open the door. You rip the tube-mask from your mouth and cough out a gush of coolant liquid. The drops collect into spheres slowly floa~ting away, lit only by the dim glow of Status LED's and emergency lights."
                     "As your memo~ries slowly return, and your brain settles into a more ratio~nal state, you care~fully make your way to the Nav-Com~puter..."])

(def init-state
  {:universe  {:reset     false
               :refsystem nil
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
   :message   {:window (into [] (reverse start-messages))
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
        (sec/change-refsystem refsystem)
        (p/move-universe))))

(defn init-universe []
  (do (log/info "initialising state")
      (let [game-state (generate-universe init-state)]
        (do (log/info "caching renderings")
            (render/cache-all (game-state :universe) (game-state :camera))
            (log/info "setting state atom")
            (reset! g/store game-state)))))