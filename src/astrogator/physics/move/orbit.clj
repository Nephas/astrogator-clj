(ns astrogator.physics.move.orbit
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]
            [astrogator.util.selectors :as s]
            [astrogator.physics.astro :as a]))

(defn cyl-to-map [parent-mappos cylpos]
  (t/add parent-mappos (t/pol-to-cart cylpos)))

(defn move-around-parent [body dt parent-mappos]
  (let [phase (get-in body [:cylpos 1])
        radius (get-in body [:cylpos 0])
        new-phase (+ phase (* dt (:cylvel body)))
        new-cylpos [radius new-phase]
        mappos (cyl-to-map parent-mappos new-cylpos)
        mapvel (t/scalar (/ 1 dt) (t/sub mappos (:mappos body)))]
    (-> body
        (assoc-in [:mappos] mappos)
        (assoc-in [:mapvel] mapvel)
        (assoc-in [:cylpos] new-cylpos))))

(defn enter-orbit [ship parent-path parent-body]
  (let [cylpos (t/cart-to-pol (t/sub (:mappos parent-body) (ship :mappos)))
        torbit (a/t-orbit (cylpos 0) :AU (:mass parent-body) :Me)]
    (-> ship
        (assoc-in [:ai-mode] :orbit)
        (assoc-in [:cylpos] cylpos)
        (assoc-in [:cylvel] (* 2 Math/PI (/ 1 torbit)))
        (assoc-in [:orbit-parent] parent-path))))

(defn leave-orbit [ship]
  (-> ship
      (assoc-in [:ai-mode] nil)
      (assoc-in [:orbit-parent] nil)))

(defn toggle-orbit [ship camera system]
  (if (nil? (ship :ai-mode))
    (enter-orbit ship (camera :target) (s/get-target camera system))
    (leave-orbit ship)))