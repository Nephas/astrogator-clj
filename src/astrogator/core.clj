(ns astrogator.core
  (:gen-class)
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [astrogator.conf :as c]
            [astrogator.render.render :as render]
            [astrogator.input.keyboard :as key]
            [astrogator.input.mouse :as mouse]
            [astrogator.gui.camera :as cam]
            [astrogator.state :as state]
            [astrogator.util.log :as log]
            [astrogator.physics.move :as p]))

(def state! (atom {}))

(defn setup []
  (q/frame-rate c/frame-rate)
  (q/color-mode :rgb)
  (q/no-stroke)
  (do (log/info "initialising state")
      (swap! state! (fn [state] (state/init-state)))
      @state!))

(defn update-state [state]
  (let [state (-> state
                  (p/move-viewsystem)
                  (cam/update-camera))]
    (do (swap! state! (fn [x] state))
        state)))

(defn draw-state [state]
  (render/render-universe (state :universe) (state :camera)))

(defn -main [& args]
  (q/defsketch astrogator
               :title "Astrogator"
               :size c/screen-size
               :setup setup

               :update update-state
               :draw draw-state

               :key-pressed key/handle-key
               :mouse-clicked mouse/handle-click
               :mouse-wheel mouse/handle-wheel

               :middleware [m/fun-mode]))
