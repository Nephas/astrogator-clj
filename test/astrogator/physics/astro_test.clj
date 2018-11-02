(ns astrogator.physics.astro-test
  (:require [clojure.test :refer :all])
  (:require [astrogator.physics.astro :refer :all])
  (:require [astrogator.util.test :refer :all]))

(deftest t-orbit-test
  (testing "orbital time calculations"
    (are [x y] (close-to x y)
               88.0 (t-orbit 0.39 :AU 1 :Msol)
               365. (t-orbit 1.00 :AU 1 :Msol)
               (* 365 12) (t-orbit 5.2 :AU 1 :Msol))))

(deftest v-orbit-test
  (testing "orbital time calculations"
    (are [x y] (close-to x y)
               7.8 (v-orbit 1 :Re 1 :Me)
               30. (v-orbit 1.00 :AU 1 :Msol))))

(deftest planet-radius-test
  (testing "orbital time calculations"
    (are [x y] (close-to x y 0.1)
               0.5 (planet-radius 0.11 :Me)
               1.0 (planet-radius 1 :Me)
               11. (planet-radius 1 :Mj))))

(deftest stefan-boltzmann-test
  (testing "radius temperature relation"
    (are [x y] (close-to x y)
               5772 (stefan-boltzmann 1 :Lsol 1 :Rsol)
               9600 (stefan-boltzmann 40 :Lsol 2.5 :Rsol))))

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