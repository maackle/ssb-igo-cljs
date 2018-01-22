(ns ssb-igo.demo
  (:require
    ))

(enable-console-print!)

(def flumeview-reduce (js/require "flumeview-reduce"))
(def pull (js/require "pull-stream"))
(def ssb-client (js/require "ssb-client"))
(def ssb-keys (js/require "ssb-keys"))

(def test-message {:type "ssb-igo-demo" :text "demo"})

(defn handle-error [err]
  (when err
    (println "ERR!" err)
    ))

(defn parse-message [msg]
  (print "°•° message received: " (js->clj msg :keywordize-keys true)))

(defn get-message [sbot]
  (pull (.createFeedStream sbot)
        (.drain pull parse-message handle-error)))

(defn pub-message [sbot msg]
  (.publish sbot (clj->js msg)
            (fn [err msg]
              (if err
                (println err)
                (println "message published:"))
              (println msg))))

(defn sbot-callback [err sbot]
  (if err
    (println "ERROR! " err)
    (do
      (println "sbot obj: " sbot)
      (get-message sbot)
      ;(pub-message test-message sbot)
      )
    )
  )
