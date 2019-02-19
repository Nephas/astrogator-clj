(ns astrogator.render.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]
            [astrogator.render.conf :as r]
            [astrogator.util.log :as log]
            [astrogator.util.string :as str]
            [astrogator.conf :as conf]))

(declare render-gui render-target-cursor)

(defn crosshair [pos color]
  (q/with-stroke [(apply q/color color) 128]
                 (do (q/stroke-weight 2)
                     (let [orientations (map #(t/scalar 20 %) '([0 1] [0 -1] [1 0] [-1 0]))]
                       (dorun (map #(q/line (t/add pos (t/scalar 1/2 %))
                                            (t/add pos %)) orientations))))))

(defn cursor [pos color]
  (q/with-stroke [(apply q/color color) 128]
                 (do (col/fill [0 0 0] 0)
                     (q/stroke-weight 2)
                     (let [size 10
                           offset (* 1/2 size)]
                       (q/rect (- (pos 0) offset) (- (pos 1) offset) size size)))))

(defn format-map [keymap]
  (apply str (map #(format "%-12s%s\n" (str (first %1)) (str/fmt-numeric (second %1))) keymap)))

(defn render-at-body [state body renderer]
  (if (nil? body)
    state
    (let [pos (t/map-to-screen (body :mappos) (state :camera))]
      (renderer pos r/gui-primary))))

(defn clean-for-format [keymap]
  (apply dissoc keymap [:surface :moons])
  ;keymap
  )

(defn render-gui [state]
  (col/fill r/gui-secondary)
  (q/text (format-map (state :time)) 50 50)
  (q/text (format-map (clean-for-format (s/get-target state))) 50 100)
  (q/text (format-map (s/get-playership state)) 50 (- (conf/screen-size 1) 300))
  (render-at-body state (s/get-refbody state) crosshair)
  (render-at-body state (s/get-target state) cursor))
