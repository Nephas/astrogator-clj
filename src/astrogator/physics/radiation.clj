(ns astrogator.physics.radiation
  (:require [clojure.math.numeric-tower :as m]
            [astrogator.util.selectors :as s]
            [quil.core :as q]
            [astrogator.physics.trafo :as t]))

(defn flux-strength-at-pos "[Lsol/AU2]" [pos bodies]
  (let [inv-dist #(/ 1 (* 4 Math/PI (m/expt (t/dist %1 %2) 2)))
        body-acc (fn [body] (* (body :luminosity) (inv-dist pos (body :mappos))))]
    (reduce + (map body-acc bodies))))

(defn flux-vec-at-pos [pos system]
  (let [bodies (s/get-bodies system)
        inv-dist #(/ 100 (+ 10 (m/expt (t/dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (body :mappos)]
                              (t/scalar (* (body :luminosity) (inv-dist pos bodypos))
                                        (t/sub pos bodypos))))]
    (reduce t/add (map body-acc bodies))))
