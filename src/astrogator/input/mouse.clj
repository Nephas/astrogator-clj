(ns astrogator.input.mouse
  (:require [astrogator.physics.trafo :as t]
            [astrogator.gui.sector :as sec]
            [astrogator.gui.camera :as cam]
            [astrogator.gui.system :as sys]
            [astrogator.util.log :as log]))

(declare handle-click handle-left handle-right)

(defn handle-click [state event]
  (let [camera (state :camera)
        screenpos [(event :x) (event :y)]]
    (case (event :button)
      :right (handle-right state screenpos camera)
      :left (handle-left state screenpos camera))))

(defn handle-right [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        viewsystem (get-in state [:universe :viewsystem])]
    (case (camera :scale)
      :sector (sec/change-viewsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-focus state (sys/get-closest-star viewsystem mappos))
      :subsystem (cam/change-focus state (sys/get-closest-planet-or-star viewsystem mappos))
      :body (cam/change-focus state (sys/get-closest-planet-or-star viewsystem mappos)))))

(defn handle-left [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        viewsystem (get-in state [:universe :viewsystem])]
    (case (camera :scale)
      :sector state
      :system (cam/change-target state (sys/get-closest-star viewsystem mappos))
      :subsystem (cam/change-target state (sys/get-closest-planet-or-star viewsystem mappos))
      :body (cam/change-target state (sys/get-closest-planet-or-star viewsystem mappos)))))

(defn handle-wheel [state event]
  (case event
    (1) (cam/zoom :in state)
    (-1) (cam/zoom :out state)
    state))