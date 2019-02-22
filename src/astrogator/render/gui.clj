(ns astrogator.render.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as r]
            [astrogator.util.rand :as rand]
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
  (map #(format "%-12s%s\n" (str (first %1)) (str/fmt-numeric (second %1))) keymap))

(defn render-at-body [state body renderer]
  (if (nil? body)
    state
    (let [pos (t/map-to-screen (:mappos body) (state :camera))]
      (renderer pos r/gui-primary))))

(defn animate-target-gui [keymap animation]
  (let [counter (int (/ (animation :target) 2))
        lines (format-map (apply dissoc keymap [:surface :moons :color]))]
    (take counter lines)))

(defn frame [lines]
  (let [border (str (apply str (repeat 16 "=")) "\n")]
    (concat [border] lines [border])))

(defn loading-screen
  ([] (q/background 0 0 0)
   (q/text "Loading" 100 100))
  ([number] (q/text (str "Loading " (apply str (repeat number ". "))) 100 100)))

(defn render-gui [state]
  (col/fill r/gui-secondary)
  (q/text (apply str (frame (format-map (state :time)))) 50 50)
  (q/text (apply str (frame (animate-target-gui (s/get-target state) (state :animation)))) 50 200)
  (q/text (apply str (frame (format-map (s/get-playership state)))) 50 (- (conf/screen-size 1) 300))
  (render-at-body state (s/get-refbody state) crosshair)
  (render-at-body state (s/get-target state) cursor))
