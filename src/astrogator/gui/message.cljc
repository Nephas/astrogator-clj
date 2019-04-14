(ns astrogator.gui.message)

(def start-messages ["You slowly drift back into con~scious~ness, cold metal and dark~ness enclo~sing you from all sides. The surging panic blocks any rea~sonable thought and you take an eternity to remem~ber..."
                     "...You're inside a Cryo-~Sarco~phague. Hastily you feel around for the emer~gency release, and bash open the door. You rip the tube-mask from your mouth and cough out a gush of coolant liquid. The drops collect into spheres slowly floa~ting away, lit only by the dim glow of Status LED's and emergency lights."
                     "As your memo~ries slowly return, and your brain settles into a more ratio~nal state, you care~fully make your way to the Nav-Com~puter..."])

(def transit-msg {:interplanetary "The magnetic hum of the ion engines permeates the whole ship."
                  :interstellar "You watch the solar sails unfold and take in the last sight of this system's sun. While you climb into the Cryo-Chamber, you remind yourself that the universe will have aged decades when you leave again."})

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

(defn push-message [state msg]
  (update-in state [:message :window] conj msg))