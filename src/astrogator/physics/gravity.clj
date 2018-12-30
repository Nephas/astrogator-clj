(ns astrogator.physics.gravity
  (:require
    [astrogator.physics.trafo :as t]
    [astrogator.util.selectors :as s]
    [clojure.math.numeric-tower :as m]))

(defn acc-at-pos [pos system]
  (let [bodies (s/get-bodies system)
        inv-dist #(/ 1 (+ 10 (m/expt (t/dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (body :mappos)]
                              (t/scalar (* (body :mass) (inv-dist pos bodypos))
                                        (t/sub bodypos pos))))]
    (reduce t/add (map body-acc bodies))))
