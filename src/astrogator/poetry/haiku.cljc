(ns astrogator.poetry.haiku
  (:require [clojure.string :as s]
            [clojure.set :as set]
            [astrogator.util.string.string :as string]
            [astrogator.poetry.lines :as l]
            [astrogator.util.util :as u]
            [astrogator.util.rand :as r]))

(def contradictions {:single    [:multiple]
                     :bright    [:dark]
                     :molten    [:wet :frozen :habitable]
                     :wet       [:dry :molten :frozen]
                     :hostile   [:habitable]

                     :multiple  [:single]
                     :dark      [:bright]
                     :frozen    [:wet :molten]
                     :dry       [:wet]
                     :habitable [:hostile :molten]})

(defn get-contradictions [tags]
  (reduce concat (map #(% contradictions) tags)))

(defn contradicts? [line tags]
  (let [contra (get-contradictions tags)]
    (zero? (count (u/intersection (:tags line) contra)))))

(defn choose-variation [options]
  (-> options
      (s/replace #"[\(\)]" "")
      (s/split #"\|")
      (r/rand-coll)))

(defn interpolate-variations [text]
  (let [words (s/split text #" ")]
    (string/join (map choose-variation words) " ")))

(defn generate-haiku [tags]
  (let [first-line (r/rand-coll (filter #(contradicts? % tags) l/first-lines))
        tags (distinct (concat tags (:tags first-line)))
        second-line (r/rand-coll (filter #(contradicts? % tags) l/second-lines))
        tags (distinct (concat tags (:tags second-line)))
        third-line (r/rand-coll (filter #(contradicts? % tags) l/third-lines))]
    (string/join (map #(interpolate-variations (:text %))
                      [first-line second-line third-line]) "\n")))