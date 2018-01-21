(ns ssb-igo.db
  (:require
   [ssb-igo.util :refer (trace tracer)]
    ))

(def index-version 0)
(def index-name "ssb-igo-index")
(def flumeview-reduce (js/require "flumeview-reduce"))

(defn get-content
  [msg]
  ;; TODO: apparently js->clj is slow, so don't do this
  (js->clj (.. msg -value -content)))

(defn is-valid-message?
  "Run basic validation on messages to see if they match schema"
  [msg]
  (let [content (get-content msg)
        type (get content "type")
        has-keys #(every? (partial contains? content) %)]
    (case type
      "igo-suggest-match"
      (has-keys ["opponent" "myColor" "gameTerms"])

      "igo-decline-match"
      (has-keys ["message"])

      "igo-move"
      (has-keys ["position" "prevMove"])

      false)))

;; CRUCIAL!
;; Allows uniform index access even if the reducer did not run
(def codec
  #js {:encode #(-> % js->clj clj->js js/JSON.stringify)
       :decode (fn [cache]
                 (let [data (-> cache js/JSON.parse)
                       val (.-value data)]
                   (do
                     (set! (.-value data) (js->clj val :keywordize-keys true))
                     data)))
       })

(defn reducer
  [index msg]
  (trace "incoming message" msg)
  (-> index
      (update :count inc)))

(defn mapper
  [msg]
  (when (is-valid-message? msg) (get-content msg)))

(defn flume-view
  [sbot]
  ; TODO: just do .use
  (._flumeUse sbot
              index-name
              (flumeview-reduce
               index-version
               reducer
               identity
               codec
               {:count 0}
               )))

(defn get-total
  [view]
  (.get view
        (fn [err index]
          (do
            (println "\nindex??" (->> index))
            (println "\nerr?? " err)
            )
          )))
