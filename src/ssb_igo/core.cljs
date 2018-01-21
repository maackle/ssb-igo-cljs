(ns ssb-igo.core
  (:require
    [ssb-igo.db :as db]
    [ssb-igo.demo :as demo]
    ))

(enable-console-print!)

(defn ^:export init
  ([sbot] (init sbot nil))
  ([sbot config]
   (let [view (db/flume-view sbot)
         get-total #(db/get-total view)]
     {:getTotal get-total}))
  )

(def exports
  {:init init
   :name "ssbIgo"
   :version "0.1"
   :manifest nil})

(defn main [& args]
  (println "!!main!!" args))

(set! *main-cli-fn* main)
