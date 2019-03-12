(ns astrogator.util.log
  (:require [astrogator.conf :as c]))

(defn log-cl [lvl msg]
  (if c/log-cl
    (do (println (str "[" lvl "]") (str msg))
        msg)))

(defn debug [msg]
  (if (= :debug c/log-level)
    (log-cl :debug msg)))

(defn info [msg]
  (if (not= :warn c/log-level)
    (log-cl :info msg)))

(defn warn [msg]
  (log-cl :warn msg))
