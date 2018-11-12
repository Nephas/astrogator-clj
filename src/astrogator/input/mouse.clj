(ns astrogator.input.mouse
  (:require [astrogator.physics.trafo :as t]
            [astrogator.gui.sector :as sec]
            [astrogator.gui.camera :as cam]
            [astrogator.gui.system :as sys]))

(defn handle-click [state event]
  (let [screenpos [(event :x) (event :y)]
        camera (state :camera)
        mappos (t/screen-to-map screenpos camera)
        viewsystem (get-in state [:universe :viewsystem])]
    (case (camera :scale)
      :sector (sec/change-viewsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-focus state (sys/get-closest-star viewsystem mappos))
      :subsystem (cam/change-focus state (sys/get-closest-planet-or-star viewsystem mappos))
      :body (cam/change-focus state (sys/get-closest-planet-or-star viewsystem mappos)))))

(defn handle-wheel [state event]
  (case event
    ( 1) (cam/zoom :in state)
    (-1) (cam/zoom :out state)
    state))