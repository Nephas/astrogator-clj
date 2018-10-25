(ns astrogator.input.mouse
  (:require [astrogator.render.render :as r]
            [astrogator.physics.trafo :as t]
            [astrogator.gui.sector :as sec]
            [astrogator.gui.camera :as cam]
            [astrogator.gui.system :as sys]))

(defn handle-click [state event]
  (let [screenpos [(event :x) (event :y)]
        camera (state :camera)
        mappos (t/screen-to-map screenpos camera)]
    (case (camera :scale)
      :sector (sec/change-viewsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-focus state (sys/get-closest-star (get-in state [:universe :viewsystem]) mappos)))))

(defn handle-wheel [state event]
  (case event
    (1) (cam/zoom :in state)
    (-1) (cam/zoom :out state)
    state))