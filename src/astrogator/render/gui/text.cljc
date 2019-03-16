(ns astrogator.render.gui.text
  (:require [astrogator.util.color :as col]
            [quil.core :as q]
            [astrogator.render.conf :as r]
            [astrogator.util.string.string :as string]))

(defn framed-lines
  ([wrapped-text width] (let [border (str (apply str (repeat width "═")))]
                          (string/join [border wrapped-text border] "\n"))))

(defn get-textbox-renderer
  ([text [x1 y1] [x2 y2]] (fn [] (do (col/fill r/gui-secondary 255)
                                     (q/text text x1 y1 x2 y2))))
  ([text [x y]] (fn [] (do (col/fill r/gui-secondary 255)
                           (q/text text x y))))
  ([text] (get-textbox-renderer text [0 0])))

(defn wrap-lines [text length]
  (loop [remaining-text text
         lines []]
    (if (< (count remaining-text) length)
      (conj lines remaining-text)
      (recur (subs remaining-text length)
             (conj lines (subs remaining-text 0 length))))))

(defn wrapped-text [text length]
  (string/join (wrap-lines text length) "\n"))