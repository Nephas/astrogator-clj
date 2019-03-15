(ns astrogator.util.log
  (:require [astrogator.conf :as c]))

(defn log-cl [lvl msg]
  (if c/log-cl
    (do (println (str "[" lvl "]") msg)
        msg)))

(defn debug [msg & msgs]
  (if (= :debug c/log-level)
    (log-cl :debug (apply str (cons msg msgs)))))

(defn info [msg & msgs]
  (if (not= :warn c/log-level)
    (log-cl :info (apply str (cons msg msgs)))))

(defn warn [msg & msgs]
  (log-cl :warn (apply str (cons msg msgs))))
