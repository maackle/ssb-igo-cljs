(ns ssb-igo.core
  (:require
    [ssb-igo.db :as db]
    [ssb-igo.demo :as demo]
    ))

(enable-console-print!)



(defn ^:export init
  ([sbot] (init sbot nil))
  ([sbot config]
   (let [view (db/flume-view sbot)]
     (println "hey a view" view)
     {:getTotal #(db/get-total view)}))
  )

(defn ^:export run-demo
  []
  (let [sbot-create (-> (js/require "scuttlebot")
                        (.use (clj->js {:init init})) )
        config (clj->js demo/sbot-config)
        sbot (sbot-create config)
        _ (println "the new sbot" sbot)
        api (aget sbot db/index-name)
        ]
    (println "the api" api)
    )
  )

(defn main [& args]
  (println "!!main!!" args))

(set! *main-cli-fn* main)
