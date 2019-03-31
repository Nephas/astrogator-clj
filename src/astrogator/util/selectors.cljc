(ns astrogator.util.selectors)

(def star? #(contains? % :luminosity))

(def planet? #(and (contains? % :seed) (not (star? %))))

(defn get-system-by-seed [state|sector seed]
  (let [systems (if (seq? state|sector) state|sector (get-in state|sector [:universe :sector]))]
    (first (filter #(= seed (:seed %)) systems))))

(defn get-expanded-refsystem [state]
  (get-in state [:universe :refsystem]))

(defn get-sector [state]
  (get-in state [:universe :sector]))

(defn get-targetsystem
  ([state] (get-system-by-seed state (get-in state [:camera :targetsystem]))))

(defn get-refsystem
  ([state] (get-system-by-seed state (get-in state [:camera :refsystem]))))

(defn get-body-by-path [path refsystem]
  (if (nil? path) nil (get-in refsystem path)))

(defn get-refbody
  ([camera refsystem]
   (get-body-by-path (camera :refbody) refsystem))
  ([state] (get-refbody (state :camera) (get-expanded-refsystem state))))

(defn get-targetbody
  ([camera refsystem]
   (get-body-by-path (camera :targetbody) refsystem))
  ([state] (get-targetbody (state :camera) (get-expanded-refsystem state))))

(def playership-path [:universe :refsystem :ships 0])

(defn get-playership [state]
  (get-in state playership-path))

(defn get-player-orbit-body [state]
  (let [path (get-in (get-playership state) [:orbit :parent])
        system (get-expanded-refsystem state)]
    (get-body-by-path path system)))

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