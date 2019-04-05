(ns astrogator.physics.thermal.thermal
  (:require
    [astrogator.physics.thermal.radiation :as r]
    [astrogator.physics.thermal.climate :as c]
    [astrogator.physics.units :as unit]
    [astrogator.util.selectors :as s]
    [astrogator.physics.astro :as a]
    [astrogator.util.util :as u]))

(defn update-surface [planet]
  (if (some? (:surface planet))
    (let [climate (:climate planet)]
      (update-in planet [:surface] u/update-values c/update-tile climate))
    planet))

(defn update-planet [planet bodies]
  (let [radius (unit/conv (:radius planet) :Re :m)
        flux (unit/conv (r/flux-strength-at-pos (:mappos planet) bodies) :Lsol/AU2 :W/m2)
        total-flux (* flux (* Math/PI radius radius))
        blackbody-temp (a/stefan-boltzmann total-flux :W radius :m)
        angle (get-in planet [:orbit :cylpos 1])]
    (-> planet
        (assoc-in [:flux] flux)
        (update-in [:climate] c/update-climate blackbody-temp angle)
        (update-surface))))

(defn update-system
  ([system bodies]
   (if (some? (system :system))
     (-> system
         (update-in [:compA] update-system bodies)
         (update-in [:compB] update-system bodies)
         (u/update-all :planets update-planet bodies))
     (-> system
         (u/update-all :planets update-planet bodies)))))

(defn update-thermal [state]
  (let [bodies (s/get-bodies (get-in state [:universe :refsystem]))]
    (update-in state [:universe :refsystem] update-system bodies)))