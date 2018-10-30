(ns astrogator.physics.units)

(def units
  {:distance {:m    1
              :km   1000
              :Re   6370000
              :Rj   7.1E7
              :Rsol 6.96E8
              :AU   149597900000
              :ly   9.5E15
              :pc   3.1E16}
   :mass     {:kg   1
              :t    1000
              :Me   6E24
              :Mj   1.9E27
              :Msol 2E30}
   :time     {:s  1
              :d  86400
              :yr (* 86400 365.25)}
   :velocity {:m/s  1
              :km/s 1000
              :AU/d 149597900000/86400}})

(defn conv [size source-unit target-unit]
  (let [contains-both-units #(and (contains? (second %) source-unit)
                                  (contains? (second %) target-unit))
        measurement (first (first (filter contains-both-units (seq units))))
        factor (/ ((units measurement) source-unit)
                  ((units measurement) target-unit))]
    (if (nil? measurement) nil
                           (float (* factor size)))))