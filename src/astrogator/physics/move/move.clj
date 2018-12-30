(ns astrogator.physics.move.move
  (:require [astrogator.physics.trafo :as t]
            [astrogator.physics.gravity :as g]))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-around-parent [body dt parent-mappos]
  (let [phase (get-in body [:cylpos 1])
        radius (get-in body [:cylpos 0])
        new-phase (+ phase (* dt (body :cylvel)))
        new-cylpos [radius new-phase]
        mappos (cyl-to-map parent-mappos new-cylpos)]
    (-> body
        (assoc-in [:mappos] mappos)
        (assoc-in [:cylpos] new-cylpos))))

(defn move-in-potential [body dt system]
  (let [mapacc (g/acc-at-pos (body :mappos) system)
        intervel (t/add (t/scalar (* 1/2 dt) mapacc) (body :mapvel))
        mappos (t/add (t/scalar dt intervel) (body :mappos))
        interacc (g/acc-at-pos mappos system)
        mapvel (t/add (t/scalar dt interacc) intervel)]
    (-> body
        (assoc-in [:mapacc] mapacc)
        (assoc-in [:mapvel] mapvel)
        (assoc-in [:mappos] mappos))))

(defn move-moons [planet dt]
  (let [move (fn [moons] (mapv #(move-around-parent % dt (planet :mappos)) moons))]
    (update-in planet [:moons] move)))

(defn move-planets [planets dt parent-mappos]
  (let [move-planet-moon-system (fn [planet]
                                  (-> planet
                                      (move-around-parent dt parent-mappos)
                                      (move-moons dt)))]
    (mapv move-planet-moon-system planets)))

(defn move-particles [particles dt parent-mappos]
  (mapv #(move-around-parent % dt parent-mappos) particles))

(defn move-ships [ships dt system]
  (mapv #(move-in-potential % dt system) ships))