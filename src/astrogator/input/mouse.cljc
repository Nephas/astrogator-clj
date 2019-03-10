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
    (do (log/debug (str "mouse: click " (event :button)))
        (case (event :button)
          :right (handle-right state screenpos camera)
          :left (handle-left state screenpos camera)))))

(defn handle-right [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        refsystem (get-in state [:universe :refsystem])]
    (case (camera :scale)
      :sector (sec/change-refsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos))
      :subsystem (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos))
      :body (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos)))))

(defn handle-left [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        refsystem (get-in state [:universe :refsystem])]
    (case (camera :scale)
      :sector (sec/change-targetsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos))
      :subsystem (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos))
      :body (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos)))))

(defn handle-wheel [state event]
  (do (log/debug (str "mouse: wheel " event))
      (cond (pos? event) (cam/zoom :in state)
            (neg? event) (cam/zoom :out state)
            true state)))

(defn handle-move [state event]
  (let [screenpos [(event :x) (event :y)]
        mappos (t/screen-to-map screenpos (state :camera))]
    (assoc-in state [:camera :mouse] {:screenpos screenpos
                                      :mappos    mappos})))