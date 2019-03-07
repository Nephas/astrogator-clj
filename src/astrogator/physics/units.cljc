(ns astrogator.physics.units
  (:require [clojure.string :as s]
            [astrogator.util.string.string :as u]))

(def units
  {:distance     {:m    1
                  :km   1000
                  :Re   6370000
                  :Rj   7.1E7
                  :Rsol 6.96E8
                  :AU   149597900000
                  :ly   9.5E15
                  :pc   3.1E16}

   :mass         {:kg   1
                  :t    1000
                  :Me   6E24
                  :Mj   1.9E27
                  :Msol 2E30}

   :time         {:s  1
                  :d  86400
                  :yr (* 86400 365.25)}

   :temperature  {:K 1}

   ;derived units
   :acceleration {:g 9.81}

   :power        {:W    1
                  :Lsol 3.846e26}})

(def derived-units {:N :kg'm/s2
                    :J :kg'm2/s2
                    :W :kg'm2/s3})

(def flat-units (apply merge (vals units)))

(def key-to-str #(subs (str %) 1))

(defn parse-expt [s] (let [ints (range 0 10)
                           strings (map str ints)
                           mapping (zipmap strings ints)]
                       (get mapping s)))

(defn expand-pow [exp-unit]
  (let [exp (s/split (s/replace-first exp-unit #"[0-9]" "&$0") #"&")
        base (first exp)
        pow (if (nil? (second exp)) 1 (parse-expt (second exp)))]
    (mapv (fn [_] (keyword base)) (range pow))))

(defn parse-units [unit-key]
  (let [sep-units (map #(s/split % #"'") (s/split (key-to-str unit-key) #"/"))
        numerators (flatten (map expand-pow (first sep-units)))
        denominators (flatten (map expand-pow (second sep-units)))]
    [numerators denominators]))

(defn map-factors [[numerators denominators]]
  (/ (reduce * (map #(float (flat-units %)) numerators))
     (reduce * (map #(float (flat-units %)) denominators))))

(defn conv-factors [x f-source f-target]
  (float (* x (/ f-source f-target))))

(defn conv
  ([size source-unit target-unit]
   (if (and (contains? flat-units source-unit) (contains? flat-units target-unit))
     (let [f-source (flat-units source-unit)
           f-target (flat-units target-unit)]
       (conv-factors size f-source f-target))
     (let [f-source (map-factors (parse-units source-unit))
           f-target (map-factors (parse-units target-unit))]
       (conv-factors size f-source f-target))))
  ([source-unit target-unit]
   (conv 1 source-unit target-unit)))