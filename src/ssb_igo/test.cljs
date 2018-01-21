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


(defn -main
  []
  (let [sbot (test-sbot)
        api (-> sbot
                .-ssbIgo
                )
        ]
    (.getTotal api)
    )
  )
