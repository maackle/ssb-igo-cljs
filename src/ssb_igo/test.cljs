(ns ssb-igo.test
  (:require
   ssb-igo.core
   ssb-igo.db
   ))

(def ssb-keys (js/require "ssb-keys"))
(def ssb-config (js/require "ssb-config/inject"))

(defn env-get [name]
  (aget js/process "env" name))


(def sbot-config
  {"path" "~/.ssb"
   "caps" {"shs" (env-get "SBOT_SHS")
           "sign" (env-get "SBOT_SIGN")}})


(defn test-sbot
  []
  (let [sbot-create (-> (js/require "scuttlebot")
                        (.use (clj->js ssb-igo.core/exports)) )
        keys (. ssb-keys loadOrCreateSync "/root/.ssb/secret")
        config (as-> sbot-config $
                     (assoc $ :keys keys)
                     (assoc $ :key (env-get "SBOT_SHS"))
                     (ssb-config "ssb" $))
        sbot (sbot-create config)
        ]
    sbot
    )
  )

#_(defn ^:export demo-client []
  (let [keys (. ssb-keys loadOrCreateSync "/root/.ssb/secret")
        sbot (ssb-client nil (clj->js sbot-config) sbot-callback)]
    ))


(defn pub-message [sbot msg]
  (.publish sbot (clj->js msg)
            (fn [err msg]
              (if err
                (println err)
                (println "message published:"))
              (println msg))))

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
