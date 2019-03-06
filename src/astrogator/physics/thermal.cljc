(ns astrogator.physics.thermal
  (:require
    [astrogator.physics.radiation :as r]
    [astrogator.physics.units :as u]
    [astrogator.util.selectors :as s]
    [astrogator.physics.astro :as a]))

(defn update-temp [planet bodies]
  (let [radius (u/conv (:radius planet) :Re :m)
        flux (u/conv (r/flux-strength-at-pos (:mappos planet) bodies) :Lsol/AU2 :W/m2)
        total-flux (* flux (* Math/PI radius radius))
        temp (a/stefan-boltzmann total-flux :W radius :m)]
    (-> planet
        (assoc-in [:temp] temp)
        (assoc-in [:flux] flux))))

(defn update-planets [bodies]
  (fn [planets] (mapv #(update-temp % bodies) planets)))

(defn update-system
  ([system bodies]
   (if (some? (system :system))
     (-> system
         (update-in [:compA] update-system bodies)
         (update-in [:compB] update-system bodies)
         (update-in [:planets] (update-planets bodies)))
     (-> system
         (update-in [:planets] (update-planets bodies))))))

(defn update-thermal [state]
  (let [bodies (s/get-bodies (get-in state [:universe :refsystem]))]
    (update-in state [:universe :refsystem] update-system bodies)))