(ns ssb-igo.core
  (:require
   [ssb-igo.db :as db]
   [ssb-igo.views :as views]
   [ssb-igo.messages :as messages]
   ))

(enable-console-print!)

#_(def ^:export view db/flume-view)

(defn init
  ([sbot opts]
   (let [testing? (.-temp opts)
         view (db/build-flume-view sbot nil)
         transformer #(partial views/view-mapper % view)
         api {:getTotal (transformer views/get-total)
              :getRequests (transformer views/get-requests)
              :getOffers (transformer views/get-offers)
              :getGames (transformer views/get-games)
              }
         ]
     (clj->js (cond-> api
                      ; conditionally add test-specific functions to api
                      testing? (assoc :destroy (.-destroy view)
                                      :value #(-> view .-value)
                                      :getDb (transformer identity))
                      ))))
  )


#_(defn init
    ([sbot opts]
     (let [with-view #(% (db/build-flume-view sbot))]
       (clj->js (cond-> {:getTotal #(with-view views/get-total)}
                        (.-temp opts) (assoc :destroy (with-view #(.-destroy %)))))))
    )

(def ^:export protocol
  (let [make #(comp clj->js (partial messages/build-msg %))]
   #js {:requestMatch (make "igo-request-match")
        :expireRequest (make "igo-expire-request")
        :offerMatch (make "igo-offer-match")
        :acceptMatch (make "igo-accept-match")
        :declineMatch (make "igo-decline-match")
        :withdrawOffer (make "igo-withdraw-offer")
        :move (make "igo-move")
        :chat (make "igo-chat")
        }))

(def ^:export plugin
  #js {:init init
       :name "ssbIgoDb"
       :version "0.1"
       :manifest nil})

(defn -main [& args]
  (println "!!main!!" args))
