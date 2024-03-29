(defproject ssb-igo "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 #_[com.cognitect/transit-cljs "0.8.243"]
                 [funcool/struct "1.2.0"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.13"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {
                :main ssb-igo.core
                :output-to "target/js/inject.js"
                :output-dir "target/js/dev"
                ; :asset-path "target/js/dev"
                :target :nodejs
                :optimizations :none
                         }}
             {:id "test"
              :source-paths ["test" "src"]
              :figwheel true
              :compiler {
                         :main ssb-igo.test
                         :output-to "target/js/test.js"
                         :output-dir "target/js/test"
                         :target :nodejs
                         :optimizations :none
                         }}
             {:id "server"
              :source-paths ["src"]
              :figwheel true
              :compiler {:main figserver.core
                         :output-to "target/fig_out/figserver.js"
                         :output-dir "target/fig_out"
                         :target :nodejs
                         :optimizations :none}}
             {:id "prod"
              :source-paths ["src"]
              :compiler {
                :main ssb-igo.core
                :output-to "target/js/inject.js"
                :output-dir "target/js/prod"
                :target :nodejs
                :optimizations :simple}}]
  }

  :figwheel {}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
