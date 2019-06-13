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
            [astrogator.state.init :as i]
            [astrogator.state.global :as g]
            [astrogator.physics.move.system :as p]
            [astrogator.physics.thermal.thermal :as t]
            [astrogator.render.gui.gui :as gui]))

(defn setup []
  (do (reset! g/screen (q/current-graphics))
      (q/frame-rate c/frame-rate)
      (q/text-font (q/create-font "src/data/conthrax.ttf" 14 true))
      (q/color-mode :hsb 1.0 1.0 1.0 255)
      (q/ellipse-mode :radius)
      (q/no-stroke)
      (i/init-universe nil)))

(defn update-state [state]
  (let [new-state (-> state
                      (p/swap-refsystem)
                      (p/move-universe)
                      (t/update-thermal)
                      (cam/update-camera)
                      (ani/update-animations))]
      (reset! g/store new-state)))

(defn draw-state [state]
  (do (render/render-universe (state :universe) (state :camera))
      (gui/render-gui state)))

(defn -main [& args]
  (q/defsketch astrogator
               :title "Astrogator"
               :size [1280 960]
               :setup setup

               :update update-state
               :draw draw-state

               :key-pressed key/handle-key
               :mouse-clicked mouse/handle-click
               :mouse-wheel mouse/handle-wheel
               :mouse-moved mouse/handle-move

               :middleware [m/fun-mode]))
