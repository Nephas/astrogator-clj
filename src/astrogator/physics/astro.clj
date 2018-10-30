(ns astrogator.physics.astro
  (:require [clojure.math.numeric-tower :as m]
            [astrogator.physics.units :as u]))


(def G-in-AU³|d²|Msol 0.000296329)                          ; AU^3 d^-2 Mo^-1
(def Msol-in-Me 333000)                                     ; Mogelfaktor
(def Me-in-Msol (/ 1 Msol-in-Me))
(def pc-in-AU 206264.8)                                     ; pc in AU
(def AU-in-pc (/ 1 pc-in-AU))
(def AU-in-m 149597900000)
(def sigma 5.67e-08)                                        ; W m^-2 K^-4
(def Rsol 695000000)                                        ; m
(def Lsol 3.846e26)                                         ; Watt
(def Msol 1.988e30)                                         ; kg
(def rho 1000)                                              ; kg m^-3
(def Me 6e24)                                               ; kg
(def Re 6370000)                                            ; m
(def Rsol-in-Re (/ Rsol Re))
(def AU|d-in-km|s 1736.1)                                   ; AU/d to km/s 150000000/(86400)
(def AU|d²-in-m|s² 20.0938786)                              ; AU/d^2 to m/s^2

(def goldilocks-temp [273 373])

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

(defn titius-bode
  ([i] (titius-bode i 0.4))
  ([i rin] (+ rin (* rin (m/expt 2 i)))))

(defn mass-luminosity [mass]
  (cond (< mass 0.43) (m/expt (* 0.23 mass) 2.3)
        (< mass 2) (m/expt mass 4)
        (< mass 20) (m/expt (* 1.4 mass) 3.5)
        true (* 32000 mass)))

(defn mass-radius [mass]
  (if (> mass 1)
    (m/expt mass 0.6)
    mass))

(defn stefan-boltzmann-temp [lum-sol radius-sol]
  (let [lum-watt (* Lsol lum-sol)
        radius-m (u/conv radius-sol :Rsol :m)]
    (m/expt (/ lum-watt (* 4 Math/PI sigma (m/expt radius-m 2))) 0.25)))

(defn hill-sphere [a m M]
  (* 0.9 a (m/expt (/ m (* 3 M)) 1/3)))

(defn planet-radius [mass-Me]
  (let [mass-kg (u/conv mass-Me :Me :kg)
        rho-kg|m³ (if (< mass-Me 10)
                    5000
                    1000)]
    (* (/ 1 Re) (m/expt (/ (* 3 mass-kg) (* 4 Math/PI rho-kg|m³)) 1/3))))

(defn t-orbit-d [r-AU mass-Msol]
  (* 365 (m/sqrt (/ (m/expt r-AU 3) mass-Msol))))

(defn v-orbit-AU|d [r-AU mass-Msol]
  (/ (* 2 Math/PI r-AU) (t-orbit-d r-AU mass-Msol)))
