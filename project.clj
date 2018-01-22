(defproject ssb-igo "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [prismatic/schema "1.1.7"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.13"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :main ssb-igo.core
                :asset-path "target/js/dev"
                :output-to "target/inject.js"
                :output-dir "target/js/dev"
                :target :nodejs
                :optimizations :none
                :source-map-timestamp true}}
             #_{:id "prod"
              :source-paths ["src"]
              :compiler {
                :output-to "target/js/prod.js"
                :output-dir "target/js/prod"
                :target :nodejs
                :optimizations :simple}}]}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
