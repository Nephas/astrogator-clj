(ns astrogator.physics.gravity
  (:require
    [astrogator.physics.trafo :as t]
    [astrogator.util.selectors :as s]
    [clojure.math.numeric-tower :as m]
    [astrogator.physics.astro :as astro]
    [astrogator.physics.units :as u]))

(defn planet-to-body [planet]
  {:mappos (planet :mappos)
   :mass   (u/conv (planet :mass) :Me :Msol)})

(defn gravacc-at-pos "pos [AU]" [pos system]
  (let [stars (s/get-bodies system)
        planets (map planet-to-body (s/get-all system :planets))
        inv-dist #(/ 1 (+ 1e-10 (m/expt (t/dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (body :mappos)]
                              (t/scalar (* astro/G (body :mass) (inv-dist pos bodypos))
                                        (t/sub bodypos pos))))]
    (reduce t/add (map body-acc (concat stars planets)))))
