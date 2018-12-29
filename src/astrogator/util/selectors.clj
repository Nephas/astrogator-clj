(ns astrogator.util.selectors)

(defn get-viewsystem [state]
  (get-in state [:universe :viewsystem]))

(defn get-refbody
  ([camera viewsystem]
   (let [path (camera :refbody)]
     (get-in viewsystem path)))
  ([state] (get-refbody (state :camera) (get-viewsystem state))))

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