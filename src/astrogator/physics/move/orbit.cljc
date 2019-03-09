(ns astrogator.physics.move.orbit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.selectors :as s]
            [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]))

(defprotocol Orbit
  (orbit [this dt parent-mappos]))

(defrecord Orbit-Elements [cylvel cylpos torbit])

(defn circular-orbit
  ([parent-mass unit [radius phi]]
   (let [phi (if (nil? phi) (* 2 Math/PI (r/uniform)) phi)
         torbit (a/t-orbit radius :AU parent-mass unit)
         cylvel (* 2 Math/PI (/ 1 torbit))]
     (->Orbit-Elements cylvel [radius phi] torbit)))
  ([parent-mass cylpos] (circular-orbit parent-mass :Msol cylpos)))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-around-parent [body dt parent-mappos]
  (let [[radius phase] (get-in body [:orbit :cylpos])
        new-phase (+ phase (* dt (get-in body [:orbit :cylvel])))
        new-cylpos [radius new-phase]
        mappos (cyl-to-map parent-mappos new-cylpos)
        mapvel (t/scalar (/ 1 dt) (t/sub mappos (:mappos body)))]
    (-> body
        (assoc-in [:mappos] mappos)
        (assoc-in [:mapvel] mapvel)
        (assoc-in [:orbit :cylpos] new-cylpos))))

(defn enter-orbit [ship parent-path parent]
  (let [orbit-radius (* 0.5 (:rhill parent))
        unit (if (s/planet? parent) :Me :Msol)
        orbit (circular-orbit (:mass parent) unit [orbit-radius nil])]
    (-> ship
        (assoc-in [:ai-mode] :orbit)
        (assoc-in [:orbit-parent] parent-path)
        (assoc-in [:orbit] orbit))))

(defn leave-orbit [ship]
  (-> ship
      (assoc-in [:ai-mode] nil)
      (assoc-in [:orbit] nil)
      (assoc-in [:orbit-parent] nil)))

(defn toggle-orbit [ship camera system]
  (let [targetbody (s/get-targetbody camera system)
        in-soi? (< (t/dist (:mappos ship) (:mappos targetbody)) (:rhill targetbody))]
    (if (and (not (= :orbit (:ai-mode ship))) in-soi?)
      (enter-orbit ship (camera :targetbody) targetbody)
      (leave-orbit ship))))

(defn transit [ship camera system]
  (enter-orbit ship (camera :targetbody) (s/get-targetbody camera system)))