(ns astrogator.physics.units-test
  (:require [clojure.test :refer :all])
  (:require [astrogator.physics.units :refer :all])
  (:require [astrogator.physics.astro :refer :all])
  (:require [astrogator.util.test :refer :all]))

(deftest conv-test
  (testing "unit conversions"
    (are [x y] (close-to x y 0.01)
               10.0 (conv 10 :km :km)
               10E3 (conv 10 :km :m)
               1.00 (conv 86400 :s :d)
               0.0058 (conv 10 :km/s :AU/d)
               0.0003 (conv (const :G) :m3/kg's2 :AU3/Msol'd2)
               9.81 (conv 1 :g :m/s2))))

(deftest conv-nil-test
  (testing "non-parseable unit conversions"
    (are [x y] (= x y)
               nil (conv 10 :apples :oranges))))
