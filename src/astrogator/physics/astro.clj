(ns astrogator.physics.astro
  (:require [clojure.math.numeric-tower :as m]
            [astrogator.physics.units :as u]))


(def G "[m3/kg's2]" 6.674E-11)
(def sigma "[W/m2'K4]" 5.67e-08)

(def COLOR {:O [144 166 255]
            :B [162 188 255]
            :A [184 202 255]
            :F [203 217 255]
            :G [255 244 233]
            :K [255 226 180]
            :M [255 203 132]})

(def CLASS-COLOR {:O :red
                  :B :orange
                  :A :yellow
                  :F :lightyellow
                  :G :lightgray
                  :K :lightblue
                  :M :blue})

(defn spectral-class [temp]
  (cond (< temp 3700) :M
        (< temp 5200) :K
        (< temp 6000) :G
        (< temp 7500) :F
        (< temp 10000) :A
        (< temp 30000) :B
        true :O))

(defn imf [mass]
  (let [a (cond (< mass 0.08) 0.3
                (< mass 0.5) 1.3
                true 2.3)]
    (m/expt mass a)))

(defn titius-bode "[rin]"
  ([i] (titius-bode i 0.4))
  ([i rin] (+ rin (* rin (m/expt 2 i)))))

(defn mass-luminosity "[Lsol]" [mass]
  (cond (< mass 0.43) (m/expt (* 0.23 mass) 2.3)
        (< mass 2) (m/expt mass 4)
        (< mass 20) (m/expt (* 1.4 mass) 3.5)
        true (* 32000 mass)))

(defn mass-radius "[Rsol]" [mass]
  (if (> mass 1)
    (m/expt mass 0.6)
    mass))

(defn stefan-boltzmann "[K]" [L $L R $R]
  (let [L (u/conv L $L :W)
        R (u/conv R $R :m)]
    (m/expt (/ L (* 4 Math/PI sigma (m/expt R 2))) 0.25)))

(defn hill-sphere "[a]" [a m M]
  (* 0.9 a (m/expt (/ m (* 3 M)) 1/3)))

(defn planet-radius "[Re]" [m $m]
  (let [rho (if (< (u/conv m $m :Me) 10)
              5500
              1000)
        m (u/conv m $m :kg)]
    (u/conv (m/expt (/ m (* 4/3 Math/PI rho)) 1/3) :m :Re)))

(defn t-orbit "[d]" [r $r m $m]
  (let [r (u/conv r $r :AU)
        m (u/conv m $m :M*)]
    (u/conv (m/sqrt (/ (m/expt r 3) m)) :yr :d)))

(defn v-orbit "[km/s]" [r $r m $m]
  (/ (* 2 Math/PI (u/conv r $r :km)) (u/conv (t-orbit r $r m $m) :d :s)))
