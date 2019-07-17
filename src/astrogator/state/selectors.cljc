(ns astrogator.state.selectors
  (:require [astrogator.state.global :as g]))

(def star? #(contains? % :luminosity))

(def planet? #(and (contains? % :seed) (not (star? %))))

(defn get-system-by-seed
  ([state seed] (let [systems (get-in state [:universe :sector])]
                  (first (filter #(= seed (:seed %)) systems))))
  ([seed] (get-system-by-seed @g/store seed)))

(defn get-expanded-refsystem
  ([state] (get-in state [:universe :refsystem]))
  ([] (get-expanded-refsystem @g/store)))

(defn get-sector
  ([state] (get-in state [:universe :sector]))
  ([] (get-sector @g/store)))

(defn get-targetsystem
  ([state] (get-system-by-seed state (get-in state [:camera :targetsystem])))
  ([] (get-targetsystem @g/store)))

(defn get-refsystem
  ([state] (get-system-by-seed state (get-in state [:camera :refsystem])))
  ([] (get-refsystem @g/store)))

(defn get-body-by-path
  ([path refsystem] (if (nil? path) nil (get-in refsystem path)))
  ([path] (get-body-by-path (get-expanded-refsystem) path)))

(defn get-refbody
  ([camera refsystem]
   (get-body-by-path (camera :refbody) refsystem))
  ([state] (get-refbody (state :camera) (get-expanded-refsystem state))))

(defn get-targetbody
  ([camera refsystem]
   (get-body-by-path (camera :targetbody) refsystem))
  ([state] (get-targetbody (state :camera) (get-expanded-refsystem state))))

(def playership-path [:universe :refsystem :ships 0])

(def ships-path [:universe :refsystem :ships])

(defn get-playership [state]
  (get-in state playership-path))

(defn get-player-orbit-body [state]
  (let [path (get-in (get-playership state) [:orbit :parent])
        system (get-expanded-refsystem state)]
    (if (some? path) (assoc (get-body-by-path path system) :path path))))

(defn get-bodies [system]
  (if (nil? (system :body))
    (concat (get-bodies (system :compA)) (get-bodies (system :compB)))
    [(system :body)]))

(defn get-all [system key]
  (if (nil? (system :body))
    (concat (get-all (system :compA) key) (get-all (system :compB) key) (system key))
    (system key)))

(defn get-subsystems [system]
  (if (nil? (system :body))
    (concat (get-subsystems (system :compA)) (get-subsystems (system :compB)) [(system :system)])
    []))