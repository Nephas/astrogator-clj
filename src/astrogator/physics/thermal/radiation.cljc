(ns astrogator.physics.thermal.radiation
  (:require [astrogator.util.math :as m]
            [astrogator.state.selectors :as s]
            [astrogator.physics.trafo :as t]))

(defn flux-strength-at-pos "[Lsol/AU2]" [pos bodies]
  (let [inv-dist #(/ 1 (* 4 Math/PI (m/expt (t/v-dist %1 %2) 2)))
        body-acc (fn [body] (* (:luminosity body) (inv-dist pos (:mappos body))))]
    (reduce + (map body-acc bodies))))

(defn flux-vec-at-pos [pos system]
  (let [bodies (s/get-bodies system)
        inv-dist #(/ 100 (+ 10 (m/expt (t/v-dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (:mappos body)]
                              (t/scalar (* (:luminosity body) (inv-dist pos bodypos))
                                        (t/sub pos bodypos))))]
    (reduce t/add (map body-acc bodies))))
