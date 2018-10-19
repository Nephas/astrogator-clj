(ns astrogator.generation.system
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.generation.star :as s]
            [astrogator.generation.planet :as p]
            [quil.core :as q]))

(declare generate-system generate-subsystem get-system-luminosity)

(defn initiate-positions
  ([system]
   (if (some? (get system :system))
     (-> system
         (update-in [:compA] initiate-positions)
         (update-in [:compB] initiate-positions)
         (assoc-in [:system :phase] (rand Math/PI))
         (assoc-in [:system :mappos] [0 0]))
     (-> system
         (assoc-in [:star :cylpos] [0 0])
         (assoc-in [:star :mappos] [0 0])))))

(defn generate-system
  ([mass max-depth max-sc-orbit]
   (let [binary (zero? (r/rand-int 2))
         next-depth (dec max-depth)]
     (if (and binary (pos? next-depth))
       (generate-subsystem mass next-depth max-sc-orbit)
       (s/generate-star mass max-sc-orbit))))
  ([mass seed] (do (r/set-seed! seed)
                   (generate-system mass 4 100)))
  ([distantsystem] (generate-system (distantsystem :mass) (distantsystem :seed))))

(defn generate-subsystem [mass next-depth max-sc-orbit]
  (let [ratio (+ 0.5 (r/rand 0.4))
        massA (* mass ratio)
        massB (- mass massA)
        radiusB (* (+ 0.2 (r/rand 0.6)) max-sc-orbit)
        radiusA (* radiusB (/ massB massA))
        sc-orbitA (a/hill-sphere radiusA massA mass)
        sc-orbitB (a/hill-sphere radiusB massB mass)
        compA (generate-system massA next-depth sc-orbitA)
        compB (generate-system massB next-depth sc-orbitB)
        torbit (a/t-orbit-d (+ radiusA radiusB) mass)]
    {:system  {:mass       mass
               :luminosity (get-system-luminosity compA compB)
               :radiusA    radiusA
               :radiusB    radiusB
               :torbit     torbit
               :cylvel     (* 2 Math/PI (/ 1 torbit))}
     :compA   compA
     :compB   compB
     :planets (p/generate-planet-system mass (* 3 radiusB) (* 0.9 max-sc-orbit))}))

(defn get-system-color
  ([compA compB] (q/blend-color (get-system-color compA) (get-system-color compB) :dodge))
  ([system] (if (some? (system :star)) (get-in system [:star :color])
                                       (get-system-color (system :compA) (system :compB)))))

(defn get-system-luminosity
  ([compA compB] (apply + (filter #(not= nil %) [(get-in compA [:system :luminosity])
                                                 (get-in compA [:star :luminosity])
                                                 (get-in compB [:system :luminosity])
                                                 (get-in compB [:star :luminosity])])))
  ([system] (if (some? (system :star)) (get-in system [:star :luminosity])
                                       (get-system-luminosity (system :compA) (system :compB)))))