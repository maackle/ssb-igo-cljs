(require '[lumo.build.api :as b])

(b/build "src"
  {:main 'ssb-igo.core
   :output-to "out/inject.js"
   :optimizations :none
   :source-map false
   :target :nodejs})
