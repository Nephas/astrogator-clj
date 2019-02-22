(ns astrogator.util.rand
  (:refer-clojure :exclude [rand rand-int rand-nth])
  (:import (java.util Random)))

(defonce rng (new Random))

(defn set-seed!
  "Sets the seed of the global random number generator."
  [seed]
  (.setSeed rng seed))

(defn rand
  "Returns a random floating point number between 0 (inclusive) and
  n (default 1) (exclusive). Works like clojure.core/rand except it
  uses the seed specified in set-random-seed!."
  ([] (.nextFloat rng))
  ([n] (* n (rand))))

(defn rand-int
  "Returns a random integer between 0 (inclusive) and n (exclusive).
  Works like clojure.core/rand except it uses the seed specified in
  set-random-seed!."
  [n]
  (int (rand n)))

(defn rand-nth
  "Return a random element of the (sequential) collection L. Will have
  the same performance characteristics as nth for the given
  collection. Works like clojure.core/rand except it uses the seed
  specified in set-random-seed!."
  [coll]
  (nth coll (rand-int (count coll))))

(defn rand-range [min max]
  (let [diff (- max min)]
    (+ min (rand diff))))

(defn rand-int-range [min max]
  (let [diff (- max min)]
    (+ min (rand-int diff))))

(defn rand-bool []
  (zero? (rand-int 2)))

(defn new-seed []
  (rand-int-range 1000000 100000000))
