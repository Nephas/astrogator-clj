(ns astrogator.input.keymap
  (:require [astrogator.gui.camera :as c]
            [astrogator.util.string.format :as fmt]
            [astrogator.state.init :as i]
            [astrogator.input.actions :as a]
            [astrogator.gui.message :as m]
            [astrogator.util.string.string :as string]))

(declare printable-keymap action-map)

(defn show-help [state]
  (m/push-message state (string/join printable-keymap "\n")))

(def action-map
  {:r     {:action #(i/init-universe %)
           :help   "Restart Game"}
   :up    {:action #(update % :camera (fn [camera] (c/zoom camera :in)))
           :help   "Zoom in"}
   :down  {:action #(update % :camera (fn [camera] (c/zoom camera :out)))
           :help   "Zoom out"}
   :left  {:action #(update-in % [:time :dps] (fn [dps] (* 0.5 dps)))
           :help   "Slower Simulation"}
   :right {:action #(update-in % [:time :dps] (fn [dps] (* 2 dps)))
           :help   "Faster Simulation"}
   :space {:action #(a/transit %)
           :help   "Start Transit to Focus"}
   :t     {:action #(a/refuel-ship %)
           :help   "Refuel Ship"}
   :h     {:action #(show-help %)
           :help   "Show Help Screen"}
   :e     {:action #(a/explore %)
           :help   "Explore Orbited Object"}
   :f     {:action #(a/focus-ship %)
           :help   "Return Focus to Ship"}})

(def printable-keymap
  (let [buttons (map #(subs (str %) 1) (keys action-map))
        helps (map #(:help %) (vals action-map))]
    (map #(fmt/f-str "~@(~a~) --- ~a" %1 %2) buttons helps)))