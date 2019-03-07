(ns astrogator.render.gui.table
  (:require [quil.core :as q]
            [astrogator.util.string.string :as string]))

(defn render-columns
  ([col1 col2 [x y] sep] (do (q/text (string/join col1 "\n") x y)
                             (q/text (string/join col2 "\n") (+ sep x) y))))

(defn render-framed-keymap
  ([col1 col2 pos] (let [sep (* 8 12)
                         width 20
                         fmt-keys #(str " - " (subs (str %) 1) ":")
                         fmt-vals string/fmt-numeric
                         border (str (apply str (repeat width "=")))
                         col1 (concat [border] (map fmt-keys col1) [border])
                         col2 (concat [""] (map fmt-vals col2) [""])]
                     (render-columns col1 col2 pos sep)))
  ([keymap pos] (render-framed-keymap (keys keymap) (vals keymap) pos)))

(defn render-animated-target-gui [keymap pos animation]
  (let [counter (animation :target)]
    (render-framed-keymap (take counter (keys keymap)) (take counter (vals keymap)) pos)))
