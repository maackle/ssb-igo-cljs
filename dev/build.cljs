(require '[lumo.build.api :as b])

(b/build "src"
  {:main 'ssb-igo.core
   :output-to "target/inject.js"
   :optimizations :advanced
   :source-map false
   :target :nodejs})
