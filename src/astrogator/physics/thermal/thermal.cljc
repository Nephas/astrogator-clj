(ns astrogator.physics.thermal.thermal
  (:require
    [astrogator.physics.thermal.radiation :as r]
    [astrogator.physics.thermal.climate :as c]
    [astrogator.physics.units :as u]
    [astrogator.util.selectors :as s]
    [astrogator.physics.astro :as a]))

(defn update-planet [planet]
  (if (some? (:surface planet))
    (update-in planet [:surface] c/update-surface (:climate planet))
    planet))

(defn update-temp [planet bodies]
  (let [radius (u/conv (:radius planet) :Re :m)
        flux (u/conv (r/flux-strength-at-pos (:mappos planet) bodies) :Lsol/AU2 :W/m2)
        total-flux (* flux (* Math/PI radius radius))
        blackbody-temp (a/stefan-boltzmann total-flux :W radius :m)
        angle (get-in planet [:orbit :cylpos 1])]
    (-> planet
        (assoc-in [:flux] flux)
        (update-in [:climate] c/update-climate blackbody-temp angle)
        (update-planet))))

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