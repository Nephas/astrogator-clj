(ns astrogator.gui.message)

(defn has-messages [state]
  (not (empty? (get-in state [:message :window]))))

(defn current-message [state]
  (last (get-in state [:message :window])))

(defn message-footer [state]
  (let [number (count (get-in state [:message :window]))]
    (if (= 1 number) ["" "[ SPACE ]"]
                     ["" (str (dec number) " more Pages [ SPACE ]")])))

(defn pop-message [state]
  (update-in state [:message :window] pop))
