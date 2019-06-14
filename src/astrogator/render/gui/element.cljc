(ns astrogator.render.gui.element
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as r]
            [astrogator.render.conf :as conf]
            [astrogator.render.gui.text :as tx]))

(defn get-bar-renderer [percentage length label]
  (fn []
    (do ((tx/get-textbox-renderer label))
        (q/with-stroke [(apply q/color r/gui-secondary) 255]
                        (do (q/stroke-weight 2)
                            (col/fill r/back-color 255)
                            (q/rect 0 0 length conf/font-size)
                            (q/no-stroke)
                            (col/fill r/gui-primary 128)
                            (q/rect 0 0 (* percentage length) conf/font-size))))))

(defn crosshair []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (q/stroke-weight 2)
                     (let [orientations (map #(t/scalar 20 %) '([0 1] [0 -1] [1 0] [-1 0]))]
                       (dorun (map #(q/line (t/scalar 0.5 %) %) orientations))))))

(defn cursor []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (col/fill)
                     (q/stroke-weight 2)
                     (let [size 15
                           offset (* 0.5 size)]
                       (q/rect (- offset) (- offset) size size)))))

(defn diamond []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (col/fill)
                     (q/stroke-weight 2)
                     (let [size 10
                           offset (* 0.5 size)]
                       (q/with-rotation [(/ Math/PI 4)] (q/rect (- offset) (- offset) size size))))))

(defn map-line [a b camera]
  (let [a-screen (t/map-to-screen a camera)
        b-screen (t/map-to-screen b camera)]
    (q/with-stroke [(apply q/color r/gui-primary) 128]
                   (do (q/stroke-weight 2)
                       (q/line a-screen b-screen)))))
