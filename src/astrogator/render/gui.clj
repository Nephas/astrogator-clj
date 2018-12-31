(ns astrogator.render.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]
            [astrogator.util.log :as log]))

(declare render-gui render-target-cursor)

(defn crosshair [pos color]
  (q/with-stroke [(col/vec-to-color color) 128]
                 (do (q/stroke-weight 2)
                     (let [orientations (map #(t/scalar 20 %) '([0 1] [0 -1] [1 0] [-1 0]))]
                       (dorun (map #(q/line (t/add pos (t/scalar 1/2 %))
                                            (t/add pos %)) orientations))))))

(defn cursor [pos color]
  (q/with-stroke [(col/vec-to-color color) 128]
                 (do (col/fill [0 0 0] 0)
                     (q/stroke-weight 2)
                     (let [size 10
                           offset (* 1/2 size)]
                       (q/rect (- (pos 0) offset) (- (pos 1) offset) size size)))))

(defn format-map [keymap]
  (apply str (map #(str (first %1) ": " (second %1) "\n") keymap)))

(defn render-at-body [state body renderer]
  (if (nil? body)
    state
    (let [pos (t/map-to-screen (body :mappos) (state :camera))]
      (renderer pos [255 255 255]))))

(defn render-gui [state]
  (col/fill [192 192 192])
  (q/text (format-map (s/get-target state)) 50 50)
  (render-at-body state (s/get-refbody state) crosshair)
  (render-at-body state (s/get-target state) cursor))
