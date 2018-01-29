(ns ssb-igo.test
  (:require
   ssb-igo.core
   ssb-igo.db
   [ssb-igo.util :refer (trace)]
   ))

(def ssb-keys (js/require "ssb-keys"))
(def ssb-client (js/require "ssb-client"))
(def ssb-config (js/require "ssb-config/inject"))

(defn env-get [name]
  (aget js/process "env" name))

(def caps-shs (env-get "SBOT_SHS"))
(def caps-sign (env-get "SBOT_SIGN"))

(defn sbot-config
  ([] (sbot-config caps-shs))
  ([shs]
   {"path" "~/.ssb"
    "caps" {"shs" shs
            "sign" caps-sign}}))


(defn test-sbot
  []
  (let [sbot-create (-> (js/require "scuttlebot")
                        (.use (clj->js ssb-igo.core/exports)) )
        keys (. ssb-keys loadOrCreateSync "/root/.ssb/secret")
        config (as-> (sbot-config) $
                     (assoc $ :keys keys)
                     (ssb-config "ssb" $))
        sbot (sbot-create config)
        ]
    sbot
    )
  )

(defn test-client
  [config callback]
  (ssb-client
   nil
   (clj->js config)
   callback))

(defn pub-message [sbot msg]
  (.publish sbot (clj->js msg)
            (fn [err msg]
              (if err
                (println err)
                (trace "message published:" msg))
              )))


(defn -main
  []
  (let [sbot (test-sbot)
        _ (pub-message sbot {:type "igo-move" :position "C2" :prevMove "GOTCHA"})
        api (-> sbot
                .-ssbIgo
                )
        ]
    (.getTotal api)
    )
  )
