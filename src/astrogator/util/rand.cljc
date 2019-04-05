(ns astrogator.util.rand
  (:require [quil.core :as q]))

(defn set-seed!
  "Sets the seed of the global random number generator."
  [seed]
  (q/random-seed seed))

(defn uniform
  "Returns a random floating point number between 0 (inclusive) and
  n (default 1) (exclusive). Works like clojure.core/rand except it
  uses the seed specified in set-random-seed!."
  ([] (q/random 1.0))
  ([x] (* x (uniform)))
  ([x1 x2] (let [diff (- x1 x2)]
             (+ x2 (uniform diff)))))

(defn phase
  "Return a uniform value between 0 and 2 PI"
  [] (uniform (* 2 Math/PI)))

(defn rand-n
  "Returns a random integer between 0 (inclusive) and n (exclusive).
  Works like clojure.core/rand except it uses the seed specified in
  set-random-seed!."
  ([n] (int (uniform n)))
  ([n1 n2] (int (uniform n1 n2))))

(defn rand-coll
  "Return a random element of the (sequential) collection L. Will have
  the same performance characteristics as nth for the given
  collection. Works like clojure.core/rand except it uses the seed
  specified in set-random-seed!."
  [coll]
  (nth coll (rand-n (count coll))))

(defn rand-bool []
  (zero? (rand-n 2)))

(defn new-seed []
  (rand-n 1000000 100000000))

(defn rand-cdf
  "Given an ordered list of value->cumulative probability pairs,
  returns a value."
  ([cdf-tuples]
   (let [r (uniform)
         rand-threshold #(< r (last %))]
     (first (first (filter rand-threshold cdf-tuples)))))
  ([vals cdf] (rand-cdf (zipmap vals cdf))))

;(defn rand-gauss []
;  (let [bin-width 0.5
;        noise (uniform bin-width)
;        gauss-sample (m/sample m/gaussian (range -3.0 3.0 bin-width))
;        cdf-sample (m/integrate (m/normalize gauss-sample))]
;    (+ (rand-cdf cdf-sample) noise)))

(defn rand-gauss []
  (reduce + (take 10 (repeatedly #(uniform -1 1)))))

(defn poisson [λ]
  "for λ > 10: roughly gaussian centered at λ"
  (loop [p 1
         k 0]
    (if (< p (Math/exp (- λ))) (dec k)
                               (let [u (uniform)] (recur (* p u) (inc k))))))

(defn gauss-approx [] (* 0.1 (- (+ (uniform) (poisson 10)) (uniform) 5.0)))

(defn stellar-imf []
  (/ (+ (uniform) (poisson 4)) 4.0))

(defn planetary-imf []
  (/ (+ (uniform) (poisson 4)) 4.0))


;algorithm poisson random number (Knuth):
;init:
;Let L ← e−λ, k ← 0 and p ← 1.
;do:
;k ← k + 1.
;Generate uniform random number u in [0,1] and let p ← p × u.
;while p > L.
;return k − 1.