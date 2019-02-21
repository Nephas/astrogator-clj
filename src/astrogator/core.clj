(ns astrogator.core
  (:gen-class)
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [astrogator.conf :as c]
            [astrogator.render.render :as render]
            [astrogator.input.keyboard :as key]
            [astrogator.input.mouse :as mouse]
            [astrogator.gui.camera :as cam]
            [astrogator.gui.animation :as ani]
            [astrogator.state :as state]
            [astrogator.util.log :as log]
            [astrogator.physics.move.system :as p]
            [astrogator.physics.thermal :as t]
            [astrogator.render.gui :as gui]))

(def store (atom {}))

(defn setup []
  (q/frame-rate c/frame-rate)
  (q/color-mode :hsb 1.0 1.0 1.0 255)
  (q/text-font (q/create-font "Consolas" 14 true))
  (q/no-stroke)
  (q/ellipse-mode :radius)
  (log/info "initialising state")
  (let [init-state (p/move-viewsystem (state/init-state))]
    (do (log/info "caching renderings")
        (render/cache-all (init-state :universe) (init-state :camera))
        (log/info "setting state atom")
        (swap! store (fn [_] init-state))
        @store)))

(defn update-state [state]
  (let [state (-> state
                  (p/move-viewsystem)
                  (t/update-thermal)
                  (cam/update-camera)
                  (cam/update-playership)
                  (ani/update-animations))]
    (do (swap! store (fn [_] state))
        state)))

(defn draw-state [state]
  (do (render/render-universe (state :universe) (state :camera))
      (gui/render-gui state)))

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
               :mouse-moved mouse/handle-move

               :middleware [m/fun-mode]))
