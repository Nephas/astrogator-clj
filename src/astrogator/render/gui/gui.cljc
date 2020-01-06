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
            [astrogator.gui.message :as m]
            [astrogator.render.conf :as conf]
            [clojure.string :as str]))

(defn get-playership-sectorpos [state]
  (t/add (:mappos (s/get-playership state)) (:sectorpos (s/get-refsystem state))))

(defn render-at-mappos [state mappos renderer]
  (if (some? mappos) (let [pos (t/map-to-screen mappos (state :camera))]
                       (q/with-translation pos (renderer)))))

(defn render-clock []
  (tab/render-framed-keymap (s/get-time) [(:left c/margin) (:top c/margin)]))

(defn render-binary-clock []
  (let [binarify #(fmt/f-str "~20,'0',B" %)
        blockify (fn [bin-str] (us/join (map #(if (= \0 %) "= " "0 ") bin-str)))
        text (-> (:day (s/get-time)) (int)
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
          text (string/join lines "\n")
          height (count (str/split text #"\n"))
          line-height (* 1.5 conf/font-size)]
      (q/with-translation [(* 0.33 (q/width)) (* 0.33 (q/height))]
                          (q/rect 0 (- (/ line-height 2)) (* 0.33 (q/width)) (* height line-height))
                          ((tx/get-textbox-renderer text))))))

(defn render-playerinfo []
  (tab/render-framed-keymap (s/get-playership) [(:left c/margin) (* (/ 2 3) (q/height))]))

(defn render-targetinfo [state target-selector pos-selector]
  (let [target (target-selector state)]
    (when (some? target)
      (do (tab/render-animated-target-gui target [(:left c/margin) (* (/ 1 5) (q/height))] (state :animation))
          (let [name (if (nil? (:name target)) "unknown" (:name target))]
            (render-at-mappos state (pos-selector target) (tx/get-textbox-renderer name [20 -10])))))))

(defn render-fuel-bar [state]
  (let [{dv     :dv
         max-dv :max-dv} (s/get-playership state)
        percentage (max 0 (/ dv max-dv))]
    (q/with-translation [(:left c/margin) (* 0.6 (q/height))]
                        ((e/get-bar-renderer percentage 100 "Fuel")))))

(defn render-crosshair [state ref-selector pos-selector]
  (render-at-mappos state (pos-selector (ref-selector state)) e/crosshair))

(defn render-cursor [state target-selector pos-selector]
  (when (some? (target-selector state))
    (render-at-mappos state (pos-selector (target-selector state)) e/cursor)))

(defn render-player-diamond [state]
  (let [pos (get-playership-sectorpos state)]
    (do (render-at-mappos state pos e/diamond)
        (render-at-mappos state pos (tx/get-textbox-renderer "you" [-15 -10])))))

(defn render-ship-diamonds [state]
  (doseq [ship (get-in state s/ships-path)]
    (render-at-mappos state (:mappos ship) (tx/get-textbox-renderer (:name ship) [-15 -10]))
    (render-at-mappos state (:mappos ship) e/diamond)))

(defn render-course [state]
  (when (and (some? (s/get-targetbody state))
             (not= (s/get-targetbody state) (s/get-player-orbit-body state)))
    (let [targetpos (:mappos (s/get-targetbody state))
          shippos (:mappos (s/get-playership state))
          dist (t/v-dist targetpos shippos)
          text (tx/get-textbox-renderer (str (string/fmt-generic dist) " AU"))]
      (do (e/map-line targetpos shippos (:camera state))
          (render-at-mappos state (t/midpoint targetpos shippos) text)))))

(defn render-interstellar-course [state]
  (when (some? (s/get-targetsystem state))
    (let [targetpos (:sectorpos (s/get-targetsystem state))
          shippos (get-playership-sectorpos state)
          dist (u/conv (t/v-dist targetpos shippos) :AU :pc)
          text (tx/get-textbox-renderer (str (string/fmt-generic dist) " pc"))]
      (do (e/map-line targetpos shippos (:camera state))
          (render-at-mappos state (t/midpoint targetpos shippos) text)))))

(defn render-gui [state]
  (let [sector-gui #(do (render-targetinfo % s/get-targetsystem :sectorpos)
                        (render-crosshair % s/get-refsystem :sectorpos)
                        (render-cursor % s/get-targetsystem :sectorpos)
                        (render-player-diamond %)
                        (render-interstellar-course %))

        system-gui #(do (render-targetinfo % s/get-targetbody :mappos)
                        (render-crosshair % s/get-refbody :mappos)
                        (render-cursor % s/get-targetbody :mappos)
                        (render-ship-diamonds %)
                        (render-course %))
        body-gui #(do
                    (render-targetinfo % s/get-targetbody :mappos)
                    (render-cursor % s/get-targetbody :mappos)
                    (render-ship-diamonds %))]

    (do (col/fill c/gui-secondary)
        (render-clock)
        (render-binary-clock)
        (render-playerinfo)

        (case (get-in state [:camera :scale])
          :body (body-gui state)
          :subsystem (system-gui state)
          :system (system-gui state)
          :sector (sector-gui state))

        (render-messages state)
        (render-fuel-bar state))))
