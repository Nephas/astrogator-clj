(ns astrogator.poetry.names
  (:require [astrogator.util.rand :as r]
            [clojure.string :as s]))

(def vowels ["a" "ae" "e" "i" "ei" "o" "u" "ou" "au" "'" "y"])

(def consonants ["b" "c" "ch" "d" "f" "g" "h" "j" "k" "l" "m" "n" "p" "q" "r" "s" "t" "v" "w" "x" "z"])

(defn generate-name
  ([length] (let [offset (r/rand-n 2)
                  indices (range offset (+ offset length))
                  rand-letter #(if (zero? (mod % 2)) (r/rand-coll vowels)
                                                     (r/rand-coll consonants))]
              (s/capitalize (apply str (map rand-letter indices)))))
  ([seed length] (do (r/set-seed! seed)
                     (generate-name length))))

