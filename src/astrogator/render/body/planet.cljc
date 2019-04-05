(ns astrogator.render.body.planet
  (:require [quil.core :as q]
            [astrogator.util.hex :as h]
            [astrogator.render.tilemap :as tm]
            [astrogator.util.color :as col]
            [astrogator.render.conf :as conf]
            [astrogator.render.geometry :as geo]
            [astrogator.physics.trafo :as t]
            [astrogator.render.field :as f]))

(defn true-colors [tile colors]
  (let [{height :height
         ice    :glacier} tile
        land (not (:ocean tile))
        ice-color (assoc (colors :glacier) 2 (+ 0.8 height))
        land-color (assoc (colors :rock) 2 (+ 0.25 height))
        ocean-color (assoc (colors :ocean) 2 (max 0.5 (+ 0.4 height)))]
    (cond ice ice-color
          land land-color
          true ocean-color)))

(defn draw-surface
  ([tiles colors zoom rot]
   (q/stroke-weight 1)
   (let [scale (* 0.1 zoom)
         view-tiles (filter #(:view %) tiles)
         colors (mapv #(true-colors % colors) view-tiles)
         positions (mapv #(h/cube-to-center-pix (:pos %) scale rot) view-tiles)]
     (doall (map (fn [pos col] (q/with-translation pos (tm/draw-hex scale col rot))) positions colors)))))

(defn cast-shadow [pos phase size length]
  (col/fill conf/planet-shade-color 128)
  (q/no-stroke)
  (q/with-translation pos
                      (q/with-rotation [(+ Math/PI phase)]
                                       (q/rect 0 (* -1 size) length (* 2 size)))))

(defn moon-with-shade
  ([pos size phase]
   (q/no-stroke)
   (cast-shadow pos phase size (* 10 (q/width)))
   (geo/circle pos size conf/moon-surface-color)
   (geo/half-circle pos size phase conf/planet-night-color)))

(defn draw-planet [refbody camera]
  (doseq [moon (:moons refbody)]
    (let [pos (t/map-to-screen (:mappos moon) camera)
          size (* 0.1 (:radius moon) (camera :obj-zoom))
          phase (+ Math/PI (get-in refbody [:orbit :cylpos 1]))]
      (moon-with-shade pos size phase)))
  (let [pos (t/map-to-screen (:mappos refbody) camera)
        size (* 0.1 (:radius refbody) (camera :obj-zoom))
        phase (+ Math/PI (get-in refbody [:orbit :cylpos 1]))
        rot (get-in refbody [:rotation :angle])]
    (do (f/draw-soi refbody camera)
        (draw-surface (vals (:surface refbody)) (:color refbody) (* 0.65 size) rot)
        (geo/ring pos (* 1.1 size) conf/back-color (* 0.2 size))
        (cast-shadow pos phase size (* 10 (q/width)))
        (geo/half-circle pos size phase conf/planet-night-color))))