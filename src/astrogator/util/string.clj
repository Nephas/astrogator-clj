(ns astrogator.util.string
  (:require [astrogator.physics.trafo :as t]
            [astrogator.util.log :as log]))

(defn fmt-round [x]
  (if (and (< x 1000) (> x 0.01))
    (format "%.2f" x)
    (format "%.1E" x)))

(defn fmt-vec [[x y]]
  (format "[%s, %s]"
          (if (int? x) x (fmt-round x))
          (if (int? y) y (fmt-round y))))

(defn fmt-numeric [x]
  (try (cond (t/vec2d? x) (fmt-vec x)
             (float? x) (fmt-round x)
             true (str x))
       (catch Exception e (log/info (str "Format Exception: " x)))))
