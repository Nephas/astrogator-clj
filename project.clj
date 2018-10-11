(defproject astrogator_clj "0.1.0-SNAPSHOT"
  :description "Spaaaaace..."
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [incanter "1.9.3"]
                 [distributions "0.1.2"]
                 [quil "2.7.1"]]
  :plugins [[com.siili/lein-cucumber "1.0.7"]]
  :profiles {:dev     {:dependencies [[com.siili/lein-cucumber "1.0.7"]]}
             :uberjar {:aot :all
                       :auto-clean false}}
  :main astrogator.core)
