(ns astrogator.physics.move.orbit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.state.selectors :as s]
            [astrogator.physics.astro :as a]
            [astrogator.util.rand :as r]
            [astrogator.util.log :as log]))

(defprotocol Orbit
  (orbit-move [this dt parent-mappos]))

(defrecord Orbit-Elements [cylvel cylpos torbit parent])

(defn circular-orbit
  ([[parent-mass unit] [radius phi] parent-path]
   (let [phi (if (nil? phi) (* 2 Math/PI (r/uniform)) phi)
         torbit (a/t-orbit radius :AU parent-mass unit)
         cylvel (* 2 Math/PI (/ 1 torbit))]
     (->Orbit-Elements cylvel [radius phi] torbit parent-path)))
  ([parent-mass cylpos] (circular-orbit [parent-mass :Msol] cylpos nil)))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-around-parent [body dt parent-mappos]
  (let [{[radius phase] :cylpos
         cylvel         :cylvel} (:orbit body)
        new-phase (+ phase (* dt cylvel))
        new-cylpos [radius new-phase]
        mappos (cyl-to-map parent-mappos new-cylpos)]
    (-> body
        (assoc-in [:mappos] mappos)
        (assoc-in [:orbit :cylpos] new-cylpos))))

(defn place-in-orbit [ship parent-path parent]
  (let [orbit-radius (* 0.5 (:rhill parent))
        unit (if (s/planet? parent) :Me :Msol)
        orbit (circular-orbit [(:mass parent) unit] [orbit-radius nil] parent-path)]
    (do (log/info "placing ship in orbit around: " parent-path)
        (-> ship
            (assoc-in [:ai-mode] :orbit)
            (assoc-in [:orbit] orbit)
            (assoc-in [:interplanetary] nil)
            (assoc-in [:interstellar] nil)))))

(defn leave-orbit [ship]
  (-> ship
      (assoc-in [:ai-mode] nil)
      (assoc-in [:orbit] nil)))

(defn toggle-orbit [ship camera]
  (let [system (s/get-expanded-refsystem)
        targetbody (s/get-targetbody camera system)
        in-soi? (< (t/dist (:mappos ship) (:mappos targetbody)) (:rhill targetbody))]
    (if (and (not (= :orbit (:ai-mode ship))) in-soi?)
      (place-in-orbit ship (camera :targetbody) targetbody)
      (leave-orbit ship))))
