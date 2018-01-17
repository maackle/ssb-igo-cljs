(ns ssb-igo.core
  (:require
    [pull-stream :as pull]
    ssb-client
    ssb-keys
    ))
;; just including left-pad to demo another way of requiring node modules

(enable-console-print!)

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

(defn sbot_callback [err sbot]
    (if err
      (println "ERROR! " err)
      (do
        (println "sbot obj: " sbot)
        (get_message sbot)
        ;(pub_message test_message sbot)
        )
      )
    )

(let [keys (. ssb-keys loadOrCreateSync "/root/.ssh/secret")
      opts {"path" "~/.ssh"
            "caps" {"shs" (env-get "SBOT_SHS")
                    "sign" (env-get "SBOT_SIGN")}}
      ssb (ssb-client nil (clj->js opts) sbot_callback)]
  )
;;(.close ssb)
