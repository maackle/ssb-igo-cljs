(ns ssb-igo.core
  (:require
    [ssb-igo.db :as db]
    [ssb-igo.demo :as demo]
    ))

(enable-console-print!)

(defn ^:export init
  ([sbot] (init sbot nil))
  ([sbot config]
   (let [view (db/flume-view sbot) ]
     #js {:getTotal #(db/get-total view)}))
  )

(def exports
  #js {:init init
       :name "ssbIgo"
       :version "0.1"
       :manifest nil})

(defn main [& args]
  (println "!!main!!" args))

(set! *main-cli-fn* main)
