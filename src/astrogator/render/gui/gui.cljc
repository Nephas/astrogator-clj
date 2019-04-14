(ns astrogator.render.gui.gui
  (:require [astrogator.state.selectors :as s]
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
            [astrogator.physics.units :as u]
            [astrogator.render.sector :as sec]
            [astrogator.gui.message :as m]
            [astrogator.render.conf :as conf]))

(defn render-at-mappos [state mappos renderer]
  (if (some? mappos) (let [pos (t/map-to-screen mappos (state :camera))]
                       (q/with-translation pos (renderer)))))

(defn render-clock [state]
  (tab/render-framed-keymap (state :time) [(:left c/margin) (:top c/margin)])
  state)

(defn render-binary-clock [state]
  (let [binarify #(fmt/f-str "~20,'0',B" %)
        blockify (fn [bin-str] (us/join (map #(if (= \0 %) "= " "0 ") bin-str)))
        text (-> (get-in state [:time :day]) (int)
                 (binarify)
                 (blockify))
        offset (* 0.5 (q/text-width (blockify (binarify 0))))]
    (q/with-translation [(- (* 0.5 (q/width)) offset) (:top c/margin)]
                        ((tx/get-textbox-renderer text)))))

(defn render-messages [state]
  (when (m/has-messages state)
    (col/fill conf/gui-back)
    (q/no-stroke)
    (let [width (* 0.33 (q/width))
          lines (-> (m/current-message state)
                    (tx/wrap-by-syllable width)
                    (concat (m/message-footer state))
                    (tx/framed-lines width))
          height (count lines)
          text (string/join lines "\n")]
      (q/with-translation [(* 0.33 (q/width)) (* 0.33 (q/height))]
                          (q/rect 0 0 (* 0.33 (q/width)) (* height 1.12 (+ (q/text-ascent) (q/text-descent))))
                          ((tx/get-textbox-renderer text))))))

(defn render-playerinfo [state]
  (tab/render-framed-keymap (s/get-playership state) [(:left c/margin) (* (/ 2 3) (q/height))])
  state)

(defn render-targetinfo [state target-selector pos-selector]
  (let [target (target-selector state)]
    (when (some? target)
      (do (tab/render-animated-target-gui target [(:left c/margin) (* (/ 1 5) (q/height))] (state :animation))
          (let [name (if (nil? (:name target)) "unknown" (:name target))]
            (render-at-mappos state (pos-selector target) (tx/get-textbox-renderer name [20 -10]))))))
  state)

(defn render-crosshair [state ref-selector pos-selector]
  (render-at-mappos state (pos-selector (ref-selector state)) e/crosshair)
  state)

(defn render-cursor [state target-selector pos-selector]
  (when (some? (target-selector state))
    (render-at-mappos state (pos-selector (target-selector state)) e/cursor))
  state)

(defn render-diamond [state target-selector scale]
  (let [pos (if (= scale :sector) (sec/get-playership-sectorpos state)
                                  (:mappos (target-selector state)))]
    (do (render-at-mappos state pos e/diamond)
        (render-at-mappos state pos (tx/get-textbox-renderer "you" [-15 -10]))
        state)))

(defn render-course [state]
  (when (and (some? (s/get-targetbody state))
             (not= (s/get-targetbody state) (s/get-player-orbit-body state)))
    (let [targetpos (:mappos (s/get-targetbody state))
          shippos (:mappos (s/get-playership state))
          dist (t/dist targetpos shippos)
          text (tx/get-textbox-renderer (str (string/fmt-generic dist) " AU"))]
      (do (e/map-line targetpos shippos (:camera state))
          (render-at-mappos state (t/midpoint targetpos shippos) text))))
  state)

(defn render-interstellar-course [state]
  (when (some? (s/get-targetsystem state))
    (let [targetpos (:sectorpos (s/get-targetsystem state))
          shippos (sec/get-playership-sectorpos state)
          dist (u/conv (t/dist targetpos shippos) :AU :pc)
          text (tx/get-textbox-renderer (str (string/fmt-generic dist) " pc"))]
      (do (e/map-line targetpos shippos (:camera state))
          (render-at-mappos state (t/midpoint targetpos shippos) text))))
  state)

(defn render-gui [state]
  (let [sector-gui #(-> %
                        (render-targetinfo s/get-targetsystem :sectorpos)
                        (render-crosshair s/get-refsystem :sectorpos)
                        (render-cursor s/get-targetsystem :sectorpos)
                        (render-diamond s/get-playership :sector)
                        (render-interstellar-course))
        system-gui #(-> %
                        (render-targetinfo s/get-targetbody :mappos)
                        (render-crosshair s/get-refbody :mappos)
                        (render-cursor s/get-targetbody :mappos)
                        (render-diamond s/get-playership :system)
                        (render-course))
        body-gui #(-> %
                      (render-targetinfo s/get-targetbody :mappos)
                      (render-cursor s/get-targetbody :mappos)
                      (render-diamond s/get-playership :body))]

    (col/fill c/gui-secondary)
    (case (get-in state [:camera :scale])
      :body (body-gui state)
      :subsystem (system-gui state)
      :system (system-gui state)
      :sector (sector-gui state))
    (render-clock state)
    (render-binary-clock state)
    (render-messages state)
    (render-playerinfo state)))
