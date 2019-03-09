(ns astrogator.render.gui.element
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as r]))

(defn crosshair []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (q/stroke-weight 2)
                     (let [orientations (map #(t/scalar 20 %) '([0 1] [0 -1] [1 0] [-1 0]))]
                       (dorun (map #(q/line (t/scalar 0.5 %) %) orientations))))))

(defn cursor []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (col/fill [0 0 0] 0)
                     (q/stroke-weight 2)
                     (let [size 15
                           offset (* 0.5 size)]
                       (q/rect (- offset) (- offset) size size)))))

(defn diamond []
  (q/with-stroke [(apply q/color r/gui-primary) 128]
                 (do (col/fill [0 0 0] 0)
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

(defn get-textbox-renderer
  ([text [x y]] (fn [] (do (col/fill r/gui-secondary 255)
                           (q/text text x y))))
  ([text] (get-textbox-renderer text [0 0])))
