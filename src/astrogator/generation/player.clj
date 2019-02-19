(ns astrogator.generation.player)

(defn generate-playership []
  {:type         :player
   :ai-mode      nil
   :orbit-parent nil
   :throttle     0
   :thrust       0.0001
   :pointing     0
   :mapvel       [0.001 -0.001]
   :mappos       [0 0]})