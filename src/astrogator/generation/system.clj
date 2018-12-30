(ns astrogator.generation.system
  (:require [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.util.color :as col]
            [astrogator.generation.star :as s]
            [astrogator.generation.planet :as p]
            [astrogator.physics.trafo :as t]))

(declare generate-system generate-subsystem get-system-luminosity)

(defn place-playership [system pos]
  (let [ship {:type   :player
              :mapvel [0.1 -0.1]
              :mappos (t/pol-to-cart pos)}]
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
  ([mass max-depth max-sc-orbit]
   (let [binary (zero? (r/rand-int 2))
         next-depth (dec max-depth)]
     (if (and binary (pos? next-depth))
       (generate-subsystem mass next-depth max-sc-orbit)
       (s/generate-star mass (* 0.75 max-sc-orbit)))))
  ([mass seed] (do (r/set-seed! seed)
                   (generate-system mass 3 (* 100 mass))))
  ([distantsystem] (generate-system (distantsystem :mass) (distantsystem :seed))))

(defn generate-subsystem [mass next-depth max-sc-orbit]
  (let [massA (* mass (r/rand-range 0.5 0.9))
        massB (- mass massA)
        radiusB (* 1.5 max-sc-orbit (r/rand-range 0.2 0.6))
        radiusA (* radiusB (/ massB massA))
        sc-orbitA (a/hill-sphere (+ radiusA radiusB) massA massB)
        sc-orbitB (a/hill-sphere (+ radiusA radiusB) massB massA)
        compA (generate-system massA next-depth (* 0.75 sc-orbitA))
        compB (generate-system massB next-depth (* 0.75 sc-orbitB))
        torbit (a/t-orbit (+ radiusA radiusB) :AU mass :Msol)]
    (conj {:system {:mass       mass
                    :rhill      max-sc-orbit
                    :luminosity (get-system-luminosity compA compB)
                    :radiusA    radiusA
                    :radiusB    radiusB
                    :torbit     torbit
                    :cylvel     (* 2 Math/PI (/ 1 torbit))}
           :compA  compA
           :compB  compB}
          (p/generate-planet-system mass (* 1.5 radiusB) (* 0.8 max-sc-orbit)))))

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