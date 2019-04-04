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
    (do (log/debug "mouse: click " (event :button))
        (case (event :button)
          :right (handle-right state screenpos camera)
          :left (handle-left state screenpos camera)
          :center state))))

(defn handle-right [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        threshold (t/screen-dist-to-map 25 camera)
        refsystem (get-in state [:universe :refsystem])]
    (do (log/info threshold)
        (case (camera :scale)
          :sector state
          :system (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos threshold))
          :subsystem (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos threshold))
          :body (cam/change-refbody state (sys/get-closest-planet-or-star refsystem mappos))))))

(defn handle-left [state screenpos camera]
  (let [mappos (t/screen-to-map screenpos camera)
        refsystem (get-in state [:universe :refsystem])]
    (case (camera :scale)
      :sector (sec/change-targetsystem state (sec/get-closest-system (get-in state [:universe :sector]) mappos))
      :system (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos))
      :subsystem (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos))
      :body (cam/change-targetbody state (sys/get-closest-planet-or-star refsystem mappos)))))

(defn accumulate-zoom [camera event]
  (let [acc-zoom (+ (:acc-zoom camera) event)
        zoom-step #(-> camera (cam/zoom %) (assoc :acc-zoom 0))]
    (cond (>= acc-zoom 1) (zoom-step :in)
          (<= acc-zoom -1) (zoom-step :out)
          true (assoc camera :acc-zoom acc-zoom))))

(defn handle-wheel [state event]
  (do (log/debug "mouse: wheel " event)
      (update state :camera accumulate-zoom event)))

(defn handle-move [state event]
  (let [screenpos [(event :x) (event :y)]
        mappos (t/screen-to-map screenpos (state :camera))]
    (assoc-in state [:camera :mouse] {:screenpos screenpos
                                      :mappos    mappos})))