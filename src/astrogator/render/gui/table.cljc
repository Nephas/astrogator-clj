(ns astrogator.render.gui.table
  (:require [quil.core :as q]
            [astrogator.util.string.string :as string]
            [astrogator.render.gui.text :as tx]))

(defn render-columns
  ([col1 col2 [x y] sep] (do (q/text (string/join col1 "\n") x y)
                             (q/text (string/join col2 "\n") (+ sep x) y))))

(defn render-framed-keymap
  ([col1 col2 pos] (let [width 300
                         fmt-keys #(str " - " (string/cut (subs (str %) 1) 8) ":")
                         fmt-vals string/fmt-generic
                         col1 (tx/framed-lines (map fmt-keys col1) width)
                         col2 (concat [""] (map fmt-vals col2) [""])]
                     (render-columns col1 col2 pos (* 0.4 width))))
  ([keymap pos] (render-framed-keymap (keys keymap) (vals keymap) pos)))

(defn render-animated-target-gui [keymap pos animation]
  (let [counter (animation :target)]
    (render-framed-keymap (take counter (keys keymap)) (take counter (vals keymap)) pos)))
