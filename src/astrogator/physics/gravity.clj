(ns astrogator.physics.gravity
  (:require
    [astrogator.physics.trafo :as t]
    [clojure.math.numeric-tower :as m]))

(defn get-bodies [system]
  (if (nil? (system :body))
    (concat (get-bodies (system :compA)) (get-bodies (system :compB)))
    [(system :body)]))

(defn acc-at-pos [pos system]
  (let [bodies (get-bodies system)
        inv-dist #(/ 1000 (+ 10 (m/expt (t/dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (body :mappos)]
                              (t/scalar (* (body :mass) (inv-dist pos bodypos))
                                        (t/sub bodypos pos))))]
    (reduce t/add (map body-acc bodies))))

(defn flux-at-pos [pos system]
  (let [bodies (get-bodies system)
        inv-dist #(/ 100 (+ 10 (m/expt (t/dist %1 %2) 3)))
        body-acc (fn [body] (let [bodypos (body :mappos)]
                              (t/scalar (* (body :luminosity) (inv-dist pos bodypos))
                                        (t/sub pos bodypos))))]
    (reduce t/add (map body-acc bodies))))

; G*M/(x1 - x2)^2