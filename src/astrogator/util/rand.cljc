(ns astrogator.util.rand
  (:require [astrogator.physics.trafo :as t]))

(declare rand-n)

(def seed (atom 0))

(def pars {:size (Math/pow 2 32)
           :mult 1664525
           :inc  1013904223})

(defn next-linear-congruential []
  (let [n @seed
        {size :size
         mult :mult
         inc  :inc} pars]
    (reset! seed (mod (+ (* mult n) inc) size))))

(defn set-seed! [num]
  "Sets the seed of the global random number generator."
  (reset! seed num))

(defn uniform
  "Returns a random floating point number between 0 (inclusive) and
  n (default 1) (exclusive). Works like clojure.core/rand except it
  uses the seed specified in set-random-seed!."
  ([] (/ (next-linear-congruential) (:size pars)))
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
  [coll] (nth coll (rand-n (count coll))))

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

(defn poisson [λ]
  "for λ > 10: roughly gaussian centered at λ"
  (loop [p 1
         k 0]
    (if (< p (Math/exp (- λ))) (dec k)
                               (let [u (uniform)] (recur (* p u) (inc k))))))

(defn rand-gauss
  ([] (/ (+ (uniform -0.5 0.5) (- (poisson 10) 10)) 5))
  ([width] (* width (rand-gauss))))


(defn stellar-imf []

  ;(* (uniform 0.01 1) (+ (uniform) (poisson 10)))
  (* (Math/abs (rand-gauss))))

(defn planetary-imf []
  (let [giant (rand-bool)]
    (if giant
      (+ (uniform) (poisson 200))
      (/ (+ (uniform) (poisson 4)) 4.0))))

(defn rand-polar
  ([] (let [phi (phase)]
        [(Math/cos phi)
         (Math/sin phi)]))
  ([radius] (t/scalar (Math/abs (rand-gauss radius)) (rand-polar))))