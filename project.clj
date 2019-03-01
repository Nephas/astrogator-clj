(defproject astrogator_clj "0.1.0-SNAPSHOT"
  :description "Spaaaaace..."
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.520"]

                 ;Server
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]

                 ;Game
                 [quil "2.7.1"]
                 [funcool/cuerdas "2.0.5"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.15"]
            [environ/environ.lein "0.3.1"]]

  :profiles {:astro-clj  {:main         "astrogator.core"
                          :uberjar-name "astrogator-standalone.jar"
                          :aot          :all
                          :auto-clean   false}

             :server     {:main         "server.core"
                          :uberjar-name "server-standalone.jar"
                          :aot          :all}

             :astro-cljs {:hooks         [leiningen.cljsbuild]
                          :clean-targets ^{:protect false} ["resources/public/js"]
                          :cljsbuild     {:builds [{:id           "optimized"
                                                    :source-paths ["src"]
                                                    :compiler     {:main       "astrogator.core"
                                                                   :output-to  "resources/public/js/main.js"
                                                                   :output-dir "resources/public/js/optimized"
                                                                   :asset-path "js/optimized"
                                                                   :optimizations :advanced
                                                                   }}
                                                   ]}}}
  )
