(ns astrogator.generation.sector
  (:require [astrogator.util.rand :as r]
            [astrogator.physics.trafo :as t]
            [astrogator.generation.system :as sys]
            [astrogator.physics.units :as u]
            [distributions.core :as d]
            [astrogator.util.log :as log]
            [astrogator.render.gui :as gui]))

(defn get-magnitude [luminosity] (- (* -2.5 (Math/log10 (max 0.01 luminosity))) 25))

(defn generate-distant-system
  [mass seed pos]
  (let [system (sys/generate-system mass seed false)
        luminosity (sys/get-system-luminosity system)
        color (sys/get-system-color system)]
    {:sectorpos  pos
     :seed       seed
     :mass       mass
     :luminosity luminosity
     :color      color
     :magnitude  (get-magnitude luminosity)}))

(defn sort-by-brightness [system-list]
  (sort-by #(% :magnitude) system-list))

(defn log-progress [iteration number]
  (when (zero? (mod iteration (/ number 10)))
    (log/info (str "- generating systems: " iteration "/" number))
    (gui/loading-screen (int (* 10 (/ iteration number))))))

(defn generate-sector "size [pc]" [size number]
  (do (log/info (str "generating sector: size [pc] " size ", number: " number))
      (sort-by-brightness
        (let [size-AU (u/conv size :pc :AU)]
          (for [iteration (range number)]
            (do (log-progress iteration number)
                (let [seed (r/rand-int 100000000)
                      mass (+ 0.1 (d/sample (d/exponential 1)))
                      pos (t/scalar size-AU [(d/sample (d/normal 0 1)) (d/sample (d/normal 0 1))])]
                  (generate-distant-system mass seed pos))))))))

(defn generate-clouds "size [pc]" [size number]
  (log/info (str "- generating clouds: size [pc] " size ", number: " number))
  (let [size-AU (u/conv size :pc :AU)]
    (for [x (range number)]
      (let [radius-AU (u/conv (* 0.1 size (r/rand)) :pc :AU)
            pos (t/scalar size-AU [(d/sample (d/uniform -1 1)) (d/sample (d/normal 0 1))])]
        {:radius    radius-AU
         :sectorpos pos
         :color     [(r/rand-range 0.7 0.9) 0.4 0.4]}))))