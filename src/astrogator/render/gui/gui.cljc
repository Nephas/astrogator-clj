(ns astrogator.render.gui.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as c]
            [astrogator.render.gui.element :as e]
            [astrogator.render.gui.table :as tab]
            [astrogator.poetry.haiku :as h]
            [astrogator.util.rand :as r]))

(defn render-at-mappos [state mappos renderer]
  (if (not (nil? mappos)) (let [pos (t/map-to-screen mappos (state :camera))]
                            (q/with-translation pos (renderer)))))

(defn loading-screen
  ([screen] (q/with-graphics @screen (do (q/background 0 0 0)
                                         (q/text "Loading" 100 100))))
  ([number screen] (q/text (str "Loading " (apply str (repeat number ". "))) 100 100)))

(defn render-clock [state]
  (tab/render-framed-keymap (state :time) [(:left c/margin) (:top c/margin)])
  state)

(defn render-playerinfo [state]
  (tab/render-framed-keymap (s/get-playership state) [(:left c/margin) (* (/ 2 3) (q/height))])
  state)

(defn render-targetinfo [state target-selector]
  (tab/render-animated-target-gui (target-selector state) [(:left c/margin) (* (/ 1 5) (q/height))] (state :animation))
  state)

(defn render-crosshair [state ref-selector pos-selector]
  (render-at-mappos state (pos-selector (ref-selector state)) e/crosshair)
  state)

(defn render-cursor [state target-selector pos-selector]
  (render-at-mappos state (pos-selector (target-selector state)) e/cursor)
  state)

(defn render-haiku [state]
  (let [targetbody (s/get-targetbody state)
        text (str (get-in targetbody [:descriptors :poem]) "\n" (get-in targetbody [:descriptors :tags]))
        textbox (e/get-textbox-renderer text [25 25])]
    (render-at-mappos state (:mappos targetbody) textbox)))

(defn render-gui [state]
  (let [sector-gui #(-> %
                        (render-targetinfo s/get-targetsystem)
                        (render-crosshair s/get-refsystem :sectorpos)
                        (render-cursor s/get-targetsystem :sectorpos))
        system-gui #(-> %
                        (render-playerinfo)
                        (render-targetinfo s/get-targetbody)
                        (render-crosshair s/get-refbody :mappos)
                        (render-cursor s/get-targetbody :mappos))
        body-gui #(-> %
                      (system-gui)
                      (render-haiku)
                      )]

    (col/fill c/gui-secondary)
    (render-clock state)
    (case (get-in state [:camera :scale])
      :body (body-gui state)
      :subsystem (system-gui state)
      :system (system-gui state)
      :sector (sector-gui state))))
