(ns astrogator.render.gui.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as c]
            [astrogator.util.string.string :as us]
            [astrogator.util.string.format :as fmt]
            [astrogator.render.gui.element :as e]
            [astrogator.render.gui.text :as tx]
            [astrogator.render.gui.table :as tab]
            [astrogator.util.string.string :as string]
            [astrogator.util.env :as env]))

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

(defn render-binary-clock [state]
  (let [text (us/join (let [blockify #(if (= \0 %) "╦" "╩") ; "▄" "▀"
                            day (get-in state [:time :day])]
                        (map blockify (fmt/f-str "~20b" (int day)))))]
    (q/with-translation [(:left c/margin) (* 3 (:top c/margin))]
                        ((tx/get-textbox-renderer text)))))

(defn render-playerinfo [state]
  (tab/render-framed-keymap (s/get-playership state) [(:left c/margin) (* (/ 2 3) (q/height))])
  state)

(defn render-targetinfo [state target-selector pos-selector]
  (let [target (target-selector state)]
    (when (not (nil? target))
      (do (tab/render-animated-target-gui target [(:left c/margin) (* (/ 1 5) (q/height))] (state :animation))
          (let [name (if (nil? (:name target)) "unknown" (:name target))]
            (render-at-mappos state (pos-selector target) (tx/get-textbox-renderer name [10 5]))))))
  state)

(defn render-crosshair [state ref-selector pos-selector]
  (render-at-mappos state (pos-selector (ref-selector state)) e/crosshair)
  state)

(defn render-cursor [state target-selector pos-selector]
  (when (not (nil? (target-selector state)))
    (render-at-mappos state (pos-selector (target-selector state)) e/cursor))
  state)

(defn render-diamond [state target-selector pos-selector]
  (render-at-mappos state (pos-selector (target-selector state)) e/diamond)
  state)

(defn render-course [state]
  (when (not (nil? (s/get-targetbody state)))
    (let [targetpos (:mappos (s/get-targetbody state))
          shippos (:mappos (s/get-playership state))
          dist (t/dist targetpos shippos)
          text (tx/get-textbox-renderer (str (string/fmt-generic dist) " AU"))
          midpoint (t/scalar 0.5 (t/add targetpos shippos))]
      (do (e/map-line targetpos shippos (:camera state))
          (render-at-mappos state midpoint text)))))

(defn render-haiku [state]
  (when (not (nil? (s/get-targetbody state)))
    (let [targetbody (s/get-targetbody state)
          text (str (get-in targetbody [:descriptors :poem]) "\n" (get-in targetbody [:descriptors :tags]))
          textbox (tx/get-textbox-renderer text [25 25])]
      (render-at-mappos state (:mappos targetbody) textbox))))

(defn render-message-box [state]
                      ((tx/get-textbox-renderer (tx/wrapped-text "askfjflksajflkjsalkjflksajfk" 5)
                                               [(* 0.33 (q/width)) (* 0.33 (q/height))]
                                               [(* 0.33 (q/width)) (* 0.33 (q/height))]))
  state)

(defn render-gui [state]
  (let [sector-gui #(-> %
                        (render-targetinfo s/get-targetsystem :sectorpos)
                        (render-crosshair s/get-refsystem :sectorpos)
                        (render-cursor s/get-targetsystem :sectorpos))
        system-gui #(-> %
                        (render-playerinfo)
                        (render-targetinfo s/get-targetbody :mappos)
                        (render-crosshair s/get-refbody :mappos)
                        (render-cursor s/get-targetbody :mappos)
                        (render-diamond s/get-playership :mappos)
                        (render-course))
        body-gui #(-> %
                      (system-gui)
                      ;(render-haiku)
                      )]

    (col/fill c/gui-secondary)
    (render-clock state)
    (render-binary-clock state)
    (render-message-box state)
    (case (get-in state [:camera :scale])
      :body (body-gui state)
      :subsystem (system-gui state)
      :system (system-gui state)
      :sector (sector-gui state))))
