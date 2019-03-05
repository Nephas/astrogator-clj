(ns astrogator.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [astrogator.conf :as c]
            [astrogator.render.render :as render]
            [astrogator.input.keyboard :as key]
            [astrogator.input.mouse :as mouse]
            [astrogator.gui.camera :as cam]
            [astrogator.gui.animation :as ani]
            [astrogator.state :as s]
            [astrogator.physics.move.system :as p]
            [astrogator.physics.thermal :as t]
            [astrogator.render.gui.gui :as gui]))

(def store (atom s/init-state))
(def screen (atom nil))

(defn setup []
  (do (reset! screen (q/current-graphics))
      (q/frame-rate c/frame-rate)
      (enable-console-print!)
      (q/text-font (q/create-font "Consolas" 14 true))
      (q/color-mode :hsb 1.0 1.0 1.0 255)
      (q/ellipse-mode :radius)
      (q/no-stroke)
      (s/load-universe store screen)))

(defn update-state []
  (let [new-state (-> @store
                      (p/move-refsystem)
                      (t/update-thermal)
                      (cam/update-camera)
                      (cam/update-playership)
                      (ani/update-animations))]
    (if (key/reset? @store)
      (s/load-universe store screen)
      (reset! store new-state))))

(defn draw-state []
  (do (render/render-universe (@store :universe) (@store :camera))
      (gui/render-gui @store)))

(defn handler [handle]
  (fn [state event]
    (reset! store (handle state event))))

(q/defsketch -main
             :title "Astrogator"
             :size [1360 800]
             :setup setup

             :host "canvas"

             :update update-state
             :draw draw-state

             :key-pressed (handler key/handle-key)
             :mouse-clicked (handler mouse/handle-click)
             :mouse-wheel (handler mouse/handle-wheel)
             :mouse-moved (handler mouse/handle-move)

             :middleware [m/fun-mode]
             :features [:global-key-events])
