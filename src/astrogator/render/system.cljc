(ns astrogator.render.system
  (:require
    [astrogator.state.selectors :as s]
    [astrogator.render.body.body :as draw]))

(defn draw-asteroids [particles camera]
  (doseq [particle particles]
    (draw/draw-distant particle camera)))

(defn draw-ships [ships camera]
  (doseq [ship ships]
    (draw/draw-distant ship camera)))

(defn draw-planets [planets camera]
  (doseq [planet planets]
    (draw/draw-distant planet camera)))

(defn draw-stars [stars camera]
  (doseq [star stars]
    (draw/draw-distant star camera)))

(defn draw-system [system camera]
  (draw-asteroids (s/get-all system :asteroids) camera)
  (draw-planets (s/get-all system :planets) camera)
  (draw-ships (s/get-all system :ships) camera)
  (draw-stars (s/get-bodies system) camera))

(defn draw-refbody [system camera]
  (let [refbody (s/get-refbody camera system)]
    (draw-ships (s/get-all system :ships) camera)
    (when
      (satisfies? draw/Drawable refbody)
      (draw/draw-detail refbody camera))))