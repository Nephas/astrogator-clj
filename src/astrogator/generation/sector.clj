(ns astrogator.generation.sector
  (:require [astrogator.util.rand :as r]
            [astrogator.physics.trafo :as t]
            [astrogator.generation.system :as sys]
            [astrogator.physics.astro :as a]
            [astrogator.render.render :as render]
            [distributions.core :as d]
            [quil.core :as q]))

(defn get-magnitude [luminosity] (- (* -2.5 (Math/log10 (max 0.01 luminosity))) 25))

(defn generate-distant-system
  [mass seed pos]
  (let [system (sys/generate-system mass seed)
        luminosity (sys/get-system-luminosity system)]
    {:sectorpos  pos
     :seed       seed
     :mass       mass
     :luminosity luminosity
     :magnitude  (get-magnitude luminosity)
     :color      (sys/get-system-color system)}))

(defn generate-sector [size-pc number]
  (let [size-AU (* a/pc-in-AU size-pc)]
    (for [x (range number)]
      (let [mass (r/rand 10)
            seed (r/rand-int 100000000)
            pos (t/scalar size-AU [(d/sample (d/normal 0 1)) (d/sample (d/normal 0 1))])]
        (generate-distant-system mass seed pos)))))

(defn generate-clouds [size-pc number]
  (let [size-AU (* a/pc-in-AU size-pc)]
    (for [x (range number)]
      (let [radius-AU (* a/pc-in-AU (r/rand 2))
            pos (t/scalar size-AU [(d/sample (d/uniform -1 1)) (d/sample (d/normal 0 1))])]
        {:radius    radius-AU
         :sectorpos pos
         :color     (render/vec-to-color
                      [(r/rand-int-range 60 100) 40 (r/rand-int-range 60 100)])}))))