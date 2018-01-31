(ns ssb-igo.core
  (:require
    [ssb-igo.db :as db]
    ))

(enable-console-print!)

#_(def ^:export view db/flume-view)

(defn init
  ([sbot opts]
   (let [testing? (.-temp opts)
         view (db/build-flume-view sbot nil)
         ]
     (clj->js (cond-> {:getTotal #(db/get-total view %)}
                      testing? (assoc :destroy (.-destroy view)
                                      :value #(-> view .-value))
                      ))))
  )


#_(defn init
  ([sbot opts]
   (let [with-view #(% (db/build-flume-view sbot))]
     (clj->js (cond-> {:getTotal #(with-view db/get-total)}
                      (.-temp opts) (assoc :destroy (with-view #(.-destroy %)))))))
  )

(def ^:export plugin
  #js {:init init
       :name "ssbIgoDb"
       :version "0.1"
       :manifest nil})

(defn main [& args]
  (println "!!main!!" args))

(set! *main-cli-fn* main)
