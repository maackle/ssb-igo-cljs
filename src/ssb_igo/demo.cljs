(ns ssb-igo.demo
  (:require
    ))

(enable-console-print!)

(def flumeview-reduce (js/require "flumeview-reduce"))
(def pull (js/require "pull-stream"))
(def ssb-client (js/require "ssb-client"))
(def ssb-keys (js/require "ssb-keys"))

(def test_message {:type "post" :text "first clojurey post"})

(defn env-get [name]
  (aget js/process "env" name))

(defn handle_error [err]
  (when err
    (println "ERR!" err)
    ))

(defn parse_message [msg]
  (print "°•° message received: " (js->clj msg :keywordize-keys true)))

(defn get_message [sbot]
  (pull (.createFeedStream sbot)
        (.drain pull parse_message handle_error)))

(defn sbot-callback [err sbot]
  (if err
    (println "ERROR! " err)
    (do
      (println "sbot obj: " sbot)
      (get_message sbot)
      ;(pub_message test_message sbot)
      )
    )
  )

(def sbot-config
  {"path" "~/.ssb"
   "caps" {"shs" (env-get "SBOT_SHS")
           "sign" (env-get "SBOT_SIGN")}})

(defn ^:export demo-client []
  (let [keys (. ssb-keys loadOrCreateSync "/root/.ssb/secret")
        sbot (ssb-client nil (clj->js sbot-config) sbot-callback)]
    ))
