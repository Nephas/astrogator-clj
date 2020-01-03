(ns astrogator.physics.trafo
  (:require [astrogator.util.env :as env]))

(defprotocol Distance
  (dist [this other] "Euclidian distance between two similar objects"))

(defn pol-to-cart
  ([r phi] [(* r (Math/cos phi))
            (* r (Math/sin phi))])
  ([[r phi]] (pol-to-cart r phi)))

(defn cart-to-pol
  ([x y] [(Math/sqrt (+ (* x x) (* y y)))
          (Math/atan2 y x)])
  ([[x y]] (cart-to-pol y x)))

(def inv #(/ 1 %))

(def add #(mapv + %1 %2))

(def sub #(mapv - %1 %2))

(defn scalar [num v] (mapv #(* num %) v))

(defn vec2d? [x]
  (and (vector? x) (= 2 (count x)) (reduce #(and %1 %2) (map number? x))))

(def neg #(scalar -1 %))

(defn norm [v]
  (let [sqr #(* % %)]
    (Math/sqrt (+ (sqr (v 0)) (sqr (v 1))))))

(defn normalize [v]
  (scalar (inv (norm v)) v))

(defn v-dist [v1 v2]
  (let [dv (sub v1 v2)]
    (norm dv)))

(defn midpoint
  ([v1 v2] (scalar 0.5 (add v1 v2)))
  ([frac v1 v2] (add v1 (scalar frac (sub v2 v1)))))

(defn map-to-screen
  "screenpos = screen-center + zoom * (offset + mappos)"
  ([mappos offset zoom]
   (add (env/screen-center) (scalar zoom (add offset mappos))))
  ([mappos camera] (let [offset (case (camera :scale)
                                  :body (camera :mappos)
                                  :subsystem (camera :mappos)
                                  :system (camera :mappos)
                                  :sector (camera :sectorpos))]
                     (map-to-screen mappos offset (camera :dist-zoom)))))

(defn screen-to-map
  "mappos = 1/zoom * (screenpos - screen-center) - offset"
  ([screenpos offset zoom]
   (sub (scalar (inv zoom) (sub screenpos (env/screen-center))) offset))
  ([screenpos camera] (let [offset (case (camera :scale)
                                     :body (camera :mappos)
                                     :subsystem (camera :mappos)
                                     :system (camera :mappos)
                                     :sector (camera :sectorpos))]
                        (screen-to-map screenpos offset (camera :dist-zoom)))))

(defn screen-dist-to-map [screendist camera]
  (/ screendist (camera :dist-zoom)))