(ns astrogator.render.universe
  (:require [quil.core :as q]
            [astrogator.util.log :as log]
            [astrogator.render.conf :as conf]
            [astrogator.render.draw.body :as draw]
            [astrogator.state.selectors :as s]
            [astrogator.gui.camera :as c]
            [astrogator.physics.trafo :as t]))

(defn draw-each [bodies camera]
  (doseq [body bodies]
    (draw/draw-trail body camera)
    (draw/draw-distant body camera)))

(defn draw-refbody [system camera]
  (let [refbody (s/get-refbody camera system)]
    (draw-each (s/get-all system :ships) camera)
    (when
      (satisfies? draw/Drawable refbody)
      (draw/draw-detail refbody camera))))

(defn draw-system [system camera]
  (draw-each (s/get-all system :asteroids) camera)
  (draw-each (s/get-all system :planets) camera)
  (draw-each (s/get-all system :ships) camera)
  (draw-each (s/get-bodies system) camera))

(defn draw-sector [systems clouds camera]
  (do (draw-each clouds camera)
      (let [on-screen (fn [distantsystem] (c/on-screen? (t/map-to-screen (:sectorpos distantsystem) camera)))
            visible-systems (take 1000 (filter on-screen systems))]
        (draw-each visible-systems camera))))

(defn render-universe [universe camera]
  (apply q/background conf/back-color)
  (case (camera :scale)
    :body (draw-refbody (universe :refsystem) camera)
    :subsystem (draw-system (universe :refsystem) camera)
    :system (draw-system (universe :refsystem) camera)
    :sector (draw-sector (universe :sector) (universe :clouds) camera)))

(defn cache-all [universe camera]
  (try (do (log/info "caching refsystem")
           (draw-system (universe :refsystem) camera)
           (log/info "caching sector")
           (draw-sector (universe :sector) (universe :clouds) camera))
       (catch Exception e
         (log/warn "could not cache renderings"))))