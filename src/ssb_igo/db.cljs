(ns ssb-igo.db
  (:require
   [struct.core :as s]
   [ssb-igo.schemas :as schemas]
   [ssb-igo.util :refer (trace tracer)]
   ))

(def index-version 0)
(def index-name "ssb-igo-index")
(def flumeview-reduce (js/require "flumeview-reduce"))

(def initial-index
  {:games {}})

(def message-protocol
  {"igo-request-match"
   {:schema {:gameTerms schemas/GameTerms}
    :reducer (fn [db msg]
               (assoc-in db [:games (:key msg)] msg))}

   "igo-offer-match"
   {:schema {:gameTerms schemas/GameTerms
             :opponent s/string
             :opponentWhite s/boolean}}

   "igo-accept-match"
   {:schema {:message s/string}}

   "igo-decline-match"
   {:schema {:message s/string}}

   "igo-move"
   {:schema {:previousMove s/string
             :position schemas/Position}
    :reducer (fn [db msg]
               (println "MOOOOVE" msg)
               db)}
   }
)


(defn flatten-message
  "Get all necessary data into the message in a flat hierarchy"
  [msg]
  ;; TODO: js->clj is slow, eventually we'll have to change this
  ;; (maybe mod scuttlebot to stream messages as raw JSON and parse as transit??)
  (let [key (.-key msg)
        value (.-value msg)
        author (.-author value)
        content (js->clj (.-content value) :keywordize-keys true)]
    (assoc content
           :key key
           :author author
           ))
  )

(def igo-message-type?
  (set (keys message-protocol)))

(defn valid-per-schema?
  [schema msg]
  (trace "SCHEMA " schema)
  (s/valid? msg schema))


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

(defn flume-reduce-fn
  [db msg]
  (let [type (:type msg)
        reducer (get-in message-protocol [type :reducer])]
   (trace "incoming message" msg)
   (-> db (reducer msg))))

(defn flume-map-fn
  [msg]
  (when (igo-message-type? (.. msg -value -content -type))
    (let [msg (flatten-message msg)
          type (:type msg)
          schema (get-in message-protocol [type :schema])]
      (when (valid-per-schema? schema msg)
        msg))
    ))

(defn flume-view
  [sbot]
  ; TODO: just do .use
  (._flumeUse sbot
              index-name
              (flumeview-reduce
               index-version
               flume-reduce-fn
               flume-map-fn
               codec
               initial-index
               )))

; (defn view-getter
;   [view ])

(defn get-total
  [view]
  (.get view
        (fn [err index]
          (do
            (println "\nindex??" index)
            (println "\nerr?? " err)
            )
          )))
