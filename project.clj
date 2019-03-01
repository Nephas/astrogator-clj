(defproject astrogator_clj "0.1.0-SNAPSHOT"
  :description "Spaaaaace..."
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [quil "2.7.1"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.15"]]

  :profiles {:clj  {:main         "astrogator.core"
                    :uberjar-name "astrogator-standalone.jar"
                    :aot          :all
                    :auto-clean   false}

             :cljs {:hooks     [leiningen.cljsbuild]
                    :cljsbuild {:builds [{:id           "optimized"
                                          :source-paths ["src"]
                                          :compiler     {:main          "astrogator.core"
                                                         :output-to     "resources/public/js/main.js"
                                                         :output-dir    "resources/public/js/optimized"
                                                         :asset-path    "js/optimized"
                                                         :optimizations :advanced}}
                                         ]}}}
  )
