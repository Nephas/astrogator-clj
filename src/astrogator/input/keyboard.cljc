(ns astrogator.input.keyboard
  (:require [astrogator.gui.message :as m]
            [astrogator.util.log :as log]
            [quil.core :as q]
            [astrogator.input.keymap :as am]))

(def coded-keys {32 :space
                 8  :back
                 9  :tab
                 10 :enter})

(defn handle-key [state event]
  (let [coded-key (coded-keys (q/key-code))
        key (if (some? coded-key) coded-key (:key event))]
    (do (log/debug "keypress: " (q/key-code) " - " (:key event))
        (if (m/has-messages state)
          (if (= key :space) (m/pop-message state) state)
          (let [action (get-in am/action-map [key :action])]
            (if (some? action)
              (action state)
              state))))))