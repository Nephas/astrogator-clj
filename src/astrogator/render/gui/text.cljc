(ns astrogator.render.gui.text
  (:require [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.render.conf :as r]
            [clojure.string :as s]))

(defn get-border [symbol width]
  (loop [border symbol]
    (let [new-border (str border " " symbol)]
      (if (< (q/text-width new-border) width)
        (recur new-border)
        border))))

(defn framed-lines
  ([lines width] (let [border (get-border "-" width)]
                          (concat [border] lines [border]))))

(defn get-textbox-renderer
  ([text [x1 y1] [x2 y2]] (fn [] (do (col/fill r/gui-secondary 255)
                                     (q/text text x1 y1 x2 y2))))
  ([text [x y]] (fn [] (do (col/fill r/gui-secondary 255)
                           (q/text text x y))))
  ([text] (get-textbox-renderer text [0 0])))

(defn wrap-by-word [text width]
  (let [words (s/split text #" ")
        append #(s/trim (str %1 " " %2))
        append-to-last (fn [lines word] (let [index (dec (count lines))]
                                          (update lines index append word)))]
    (loop [remaining-words words
           lines []]
      (if (empty? remaining-words) lines
                                   (let [word (first remaining-words)]
                                     (recur (rest remaining-words)
                                            (if (< (q/text-width (append (last lines) word)) width)
                                              (if (empty? lines) [word]
                                                                 (append-to-last lines word))
                                              (conj lines word))))))))

(defn wrap-by-syllable [text width]
  (let [spaced-text (s/replace text #"~" "~ ")]
    (->> (wrap-by-word spaced-text width)
         (map #(s/replace % #"~ " ""))
         (map #(s/replace % #"~" "-"))
         (into []))))