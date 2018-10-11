(ns astrogator.physics.astro-test
  (:require [clojure.test :refer :all])
  (:require [astrogator.physics.astro :refer :all])
  (:require [astrogator.util.test :refer :all]))

(deftest t-orbit-test
  (testing "orbital time calculations"
    (are [x y] (close-to x y)
               88.0 (t-orbit-d 0.39 1)
               365. (t-orbit-d 1.00 1)
               (* 365 12) (t-orbit-d 5.2 1))))

(deftest stefan-boltzmann-test
  (testing "radius temperature relation"
    (are [x y] (close-to x y)
               5772 (stefan-boltzmann-temp 1 1)
               9600 (stefan-boltzmann-temp 40 2.5))))

(deftest mass-radius-test
  (testing "mass radius relation"
    (are [x y] (close-to x y)
                0.7 (mass-radius 0.7)
                1.0 (mass-radius 1.0)
                1.5 (mass-radius 2.0))))

(deftest mass-luminosity-test
  (testing "mass luminosity relation"
    (are [x y] (close-to x y 0.2)
                0.6 (mass-luminosity 0.9)
                1.0 (mass-luminosity 1.0)
                1.5 (mass-luminosity 1.1))))