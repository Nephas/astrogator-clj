(ns astrogator.render.gui
  (:require [astrogator.util.selectors :as s]
            [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]
            [astrogator.render.conf :as r]
            [astrogator.util.rand :as rand]
            [astrogator.util.string :as string]
            [astrogator.conf :as conf]
            [astrogator.util.log :as log]))

(declare render-gui render-target-cursor)

(defn crosshair [pos color]
  (q/with-stroke [(apply q/color color) 128]
                 (do (q/stroke-weight 2)
                     (let [orientations (map #(t/scalar 20 %) '([0 1] [0 -1] [1 0] [-1 0]))]
                       (dorun (map #(q/line (t/add pos (t/scalar 0.5 %))
                                            (t/add pos %)) orientations))))))

(defn cursor [pos color]
  (q/with-stroke [(apply q/color color) 128]
                 (do (col/fill [0 0 0] 0)
                     (q/stroke-weight 2)
                     (let [size 10
                           offset (* 0.5 size)]
                       (q/rect (- (pos 0) offset) (- (pos 1) offset) size size)))))

(defn format-map [keymap]
  ;(map #(format "%-12s%s\n" (str (first %1)) (str/fmt-numeric (second %1))) keymap)
  (map #(str (first %1) ": " (subs (second %1) 0 20) "\n") keymap))

(defn render-columns
  ([col1 col2 [x y] sep] (do (q/text (string/join col1 "\n") x y)
                             (q/text (string/join col2 "\n") (+ sep x) y))))

(defn render-framed-keymap
  ([col1 col2 pos] (let [sep (* 8 12)
                         width 20
                         fmt-keys #(str " - " (subs (str %) 1) ":")
                         fmt-vals #(string/cut (str %) 20)
                         border (str (apply str (repeat width "=")))
                         col1 (concat [border] (map fmt-keys col1) [border])
                         col2 (concat [""] (map fmt-vals col2) [""])]
                     (render-columns col1 col2 pos sep)))
  ([keymap pos] (render-framed-keymap (keys keymap) (vals keymap) pos)))

(defn render-at-body [state body renderer]
  (if (nil? body) state
                  (let [pos (t/map-to-screen (:mappos body) (state :camera))]
                    (renderer pos r/gui-primary))))

(defn render-at-system [state system renderer]
  (if (nil? system) state
                    (let [pos (t/map-to-screen (:sectorpos system) (state :camera))]
                      (renderer pos r/gui-primary))))

(defn render-animated-target-gui [keymap pos animation]
  (let [counter (animation :target)]
    (render-framed-keymap (take counter (keys keymap)) (take counter (vals keymap)) pos)))

(defn loading-screen
  ([screen] (q/with-graphics @screen (do (q/background 0 0 0)
                                         (q/text "Loading" 100 100))))
  ([number screen] (q/text (str "Loading " (apply str (repeat number ". "))) 100 100)))

(defn render-gui [state]
  (let [system-gui #(do (render-framed-keymap (state :time) [50 50])
                        (render-animated-target-gui (s/get-targetbody state) [50 200] (state :animation))
                        (render-framed-keymap (s/get-playership state) [50 (- (q/height) 300)])
                        (render-at-body state (s/get-refbody state) crosshair)
                        (render-at-body state (s/get-targetbody state) cursor))
        sector-gui #(do (render-framed-keymap (state :time) [50 50])
                        (render-animated-target-gui (s/get-targetsystem state) [50 200] (state :animation))
                        (render-at-system state (s/get-refsystem state) crosshair)
                        (render-at-system state (s/get-targetsystem state) cursor))]

    (col/fill r/gui-secondary)
    (case (get-in state [:camera :scale])
      :body (system-gui)
      :subsystem (system-gui)
      :system (system-gui)
      :sector (sector-gui))))
