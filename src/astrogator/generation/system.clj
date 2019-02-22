(ns astrogator.generation.system
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.util.color :as col]
            [astrogator.generation.star :as s]
            [astrogator.generation.planet.planet :as p]
            [astrogator.generation.player :as pl]))

(declare generate-system generate-subsystem get-system-luminosity)

(defn place-playership [system pos]
  (let [ship (assoc-in (pl/generate-playership) [:mappos] pos)]
    (assoc-in system [:ships] [ship])))

(defn initiate-positions
  ([system]
   (if (some? (get system :system))
     (-> system
         (update-in [:compA] initiate-positions)
         (update-in [:compB] initiate-positions)
         (assoc-in [:system :phase] (rand Math/PI))
         (assoc-in [:system :mappos] [0 0]))
     (-> system
         (assoc-in [:body :cylpos] [0 0])
         (assoc-in [:body :mappos] [0 0])))))

(defn generate-system
  ([mass max-depth max-sc-orbit planets?]
   (let [binary (r/rand-bool)
         next-depth (dec max-depth)
         sc-orbit (* 0.75 max-sc-orbit)]
     (if (and binary (pos? next-depth))
       (generate-subsystem mass next-depth sc-orbit planets?)
       (s/generate-star mass sc-orbit planets?))))
  ([mass seed planets?] (do (r/set-seed! seed) (generate-system mass 3 (* 100 mass) planets?)))
  ([mass seed] (generate-system mass seed false)))

(defn generate-subsystem [mass next-depth sc-orbit planets?]
  (let [massA (* mass (r/rand-range 0.5 0.9))
        massB (- mass massA)
        radiusB (* 1.5 sc-orbit (r/rand-range 0.2 0.6))
        radiusA (* radiusB (/ massB massA))
        dist (+ radiusA radiusB)
        sc-orbitA (a/hill-sphere dist massA massB)
        sc-orbitB (a/hill-sphere dist massB massA)
        compA (generate-system massA next-depth sc-orbitA planets?)
        compB (generate-system massB next-depth sc-orbitB planets?)
        torbit (a/t-orbit dist :AU mass :Msol)]
    (conj {:system {:mass       mass
                    :rhill      sc-orbit
                    :radiusA    radiusA
                    :radiusB    radiusB
                    :torbit     torbit
                    :luminosity (get-system-luminosity compA compB)
                    :cylvel     (* 2 Math/PI (/ 1 torbit))}
           :compA  compA
           :compB  compB}
          (if planets? (p/generate-planet-system mass (* 1.5 radiusB) sc-orbit)))))

(defn get-system-color
  ([compA compB] (col/blend-vec-color (get-system-color compA) (get-system-color compB)))
  ([system] (if (some? (system :body)) (get-in system [:body :color])
                                       (get-system-color (system :compA) (system :compB)))))

(defn get-system-luminosity
  ([compA compB] (apply + (filter #(not= nil %) [(get-in compA [:system :luminosity])
                                                 (get-in compA [:body :luminosity])
                                                 (get-in compB [:system :luminosity])
                                                 (get-in compB [:body :luminosity])])))
  ([system] (if (some? (system :body)) (get-in system [:body :luminosity])
                                       (get-system-luminosity (system :compA) (system :compB)))))