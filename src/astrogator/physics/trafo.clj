(ns astrogator.physics.trafo
  (:require [astrogator.conf :as c]))

(defn pol-to-cart
  ([r phi] [(* r (Math/cos phi))
            (* r (Math/sin phi))])
  ([[r phi]] (pol-to-cart r phi)))

(def inv #(/ 1 %))

(def add #(into [] (map + %1 %2)))

(def sub #(into [] (map - %1 %2)))

(defn scalar [num v]
  (into [] (map #(* num %) v)))

(def neg #(scalar -1 %))

(def screen-center (scalar 1/2 c/screen-size))

(defn norm [v]
  (let [sqr #(* % %)]
    (Math/sqrt (+ (sqr (v 0)) (sqr (v 1))))))

(defn dist [v1 v2]
  (let [dv (sub v1 v2)]
    (norm dv)))

(defn map-to-screen
  "screenpos = screen-center + zoom * (offset + mappos)"
  ([mappos offset zoom]
   (add screen-center (scalar zoom (add offset mappos))))
  ([mappos camera] (let [offset (case (camera :scale)
                                  :subsystem (camera :mappos)
                                  :system (camera :mappos)
                                  :sector (camera :sectorpos))]
                     (map-to-screen mappos offset (camera :dist-zoom)))))

(defn screen-to-map
  "mappos = 1/zoom * (screenpos - screen-center) - offset"
  ([screenpos offset zoom]
   (sub (scalar (inv zoom) (sub screenpos screen-center)) offset))
  ([screenpos camera] (let [offset (case (camera :scale)
                                     :subsystem (camera :mappos)
                                     :system (camera :mappos)
                                     :sector (camera :sectorpos))]
                        (screen-to-map screenpos offset (camera :dist-zoom)))))