(ns astrogator.generation.body.planet
  (:require [astrogator.physics.astro :as a]
            [astrogator.physics.units :as unit]
            [astrogator.util.rand :as r]
            [astrogator.util.log :as log]
            [astrogator.generation.system.lunar :as l]
            [astrogator.generation.body.surface :as surf]
            [astrogator.physics.move.orbit :as orb]
            [astrogator.generation.expandable :as exp]
            [astrogator.physics.move.rotate :as rot]
            [astrogator.poetry.names :as n]
            [astrogator.physics.thermal.climate :as c]
            [astrogator.render.draw.body :as draw]
            [astrogator.physics.trafo :as trafo]
            [astrogator.physics.units :as u]
            [astrogator.util.color :as col]
            [astrogator.render.field :as f]
            [astrogator.render.conf :as conf]
            [astrogator.render.draw.geometry :as geo]
            [quil.core :as q]
            [astrogator.render.tilemap :as tm]
            [astrogator.physics.trail :as trail]
            [astrogator.physics.move.orbit :as o]))

(def base-tilesize 12)

(defn get-distant-color [planet]
  (let [{rock    :rock
         glacier :glacier
         ocean   :ocean} (:color planet)]
    (col/blend-vec-color rock glacier)))

(defn get-tilesize [planet]
  (+ base-tilesize (int (:radius planet))))

(defrecord Planet [mass radius seed name rhill climate mappos color circumbinary type]

  exp/Seed
  (same? [this other] (exp/equal-by-seed this other))
  (expand [this]
    (do (log/info "extracting planet: " (:seed this))
        (r/set-seed! (:seed this))
        (let [circumbinary false
              inner-orbit (* 10 (u/conv radius :Re :AU))
              {flux    :flux
               climate :climate
               rhill   :rhill
               mass    :mass} this]
          (-> this
              (assoc :descriptors (surf/get-descriptors climate flux circumbinary))
              (assoc :surface (surf/planet-map (get-tilesize this) 0.45 4 8 0.2))
              (assoc :moons (l/generate-moon-system mass inner-orbit rhill))))))

  draw/Drawable
  (draw-distant [this camera]
    (let [pos (trafo/map-to-screen (:mappos this) camera)
          size (* 0.1 (Math/log (+ 1 (:radius this))) (camera :obj-zoom))
          phase (get-in this [:orbit :cylpos 1])
          color (draw/main-color this)]
      (do (f/draw-soi this camera conf/gui-secondary)
          (doseq [moon (:moons this)]
            (draw/draw-distant moon camera))
          (if (< size conf/airy-threshold)
            (geo/airy pos 1 color)
            (do (geo/half-circle pos size phase color))))))
  (draw-detail [this camera]
    (let [phase (+ Math/PI (get-in this [:orbit :cylpos 1]))]
      (doseq [moon (:moons this)]
        (draw/draw-detail (assoc moon :phase phase) camera))
      (let [pos (trafo/map-to-screen (:mappos this) camera)
            size (* 0.1 (:radius this) (camera :obj-zoom))]
        (do (f/draw-soi this camera)
            (draw/draw-surface this camera)
            (geo/ring pos (* 1.25 size) conf/back-color (* 0.5 size))
            (geo/cast-shadow pos phase size (* 10 (q/width)))
            (geo/half-circle pos size phase conf/planet-night-color)))))
  (draw-trail [this camera]
    (trail/draw-trail this camera (draw/main-color this)))
  (draw-surface [this camera]
    (let [scale (* 0.01 (/ base-tilesize (get-tilesize this)) (:radius this) (camera :obj-zoom))]
      (tm/draw-tilemap this scale)))
  (main-color [this] (let [{rock    :rock
                            glacier :glacier
                            ocean   :ocean} (:color this)]
                       (col/blend-vec-color rock glacier))))

(extend Planet orb/Orbit orb/orbit-impl)
(extend Planet trafo/Distance trafo/distance-impl)
(extend Planet trail/Trail trail/trail-impl)
(extend Planet rot/Rot rot/rot-impl)

(defn generate-planet [parent-mass seed orbit-radius circumbinary]
  (let [mass (r/planetary-imf)
        radius (a/planet-radius mass :Me)
        climate (c/generate-climate mass)
        rhill (a/hill-sphere orbit-radius (unit/conv mass :Me :Msol) parent-mass)
        color {:rock    [(r/uniform 0.0 0.25) 0.6 0.6]
               :ocean   [(r/uniform 0.5 0.75) 0.6 0.6]
               :glacier [(r/uniform 0.5 0.75) 0.2 0.8]}
        mappos [0 0]
        name (n/generate-name seed (r/rand-n 5 7))
        type (if (< mass 10) :rock :gas)]
    (-> (->Planet mass radius seed name rhill climate mappos color circumbinary type)
        (rot/init-at (+ (r/uniform) (r/poisson 2)))
        (o/init-orbit [parent-mass :Msol] [orbit-radius nil] nil))))
