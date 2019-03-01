(ns astrogator.render.ship
  (:require [quil.core :as q]
            [astrogator.util.color :as col]
            [astrogator.physics.trafo :as t]
            [astrogator.render.geometry :as geo]))

(defn exhaust [pos size pointing]
  (do (col/fill [0 0 1])
      (q/with-translation pos
                          (q/with-rotation [(+ Math/PI pointing)]
                                           (q/triangle (- 2) -3 (* 2 size) 0 (- 2) 3)))))

(defn triangle-ship
  ([pos size pointing throttle]
   (do (q/no-stroke)
       (exhaust pos (* 15 throttle) pointing)
       (col/fill [0 0 0.5])
       (q/with-translation pos
                           (q/with-rotation [pointing]
                                            (q/triangle (- size) (- size) (* 2 size) 0 (- size) size))))))

(defn render-ship [ship pos]
  (q/with-stroke [(apply q/color [1 1 1]) 128]
                 (let [mapvel (ship :mapvel)]
                   (do (geo/arrow pos (t/normalize mapvel) (* 10 (+ 5 (Math/log10 (t/norm mapvel)))))
                       (geo/arrow pos (t/normalize (ship :mapacc)) 20)
                       (triangle-ship pos 5 (ship :pointing) (ship :throttle))))))