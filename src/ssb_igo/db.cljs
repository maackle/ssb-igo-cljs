(ns ssb-igo.db
  (:require
   [struct.core :as st]
   [ssb-igo.schemas :as schemas]
   [ssb-igo.util :refer (trace tracer)]
   ))

(def index-version 0)
(def default-index-name "ssb-igo-index")
(def flumeview-reduce (js/require "flumeview-reduce"))

(def initial-index
  (st/validate! {:games {}
                :requests {}
                :offers {}
                :total-test 0}
   schemas/FlumeIndex))

(def message-protocol
  {"igo-request-match"
   {:schema {:gameTerms schemas/GameTerms}
    :mapper (fn [msg]
              (assoc (:content msg) :status :open))
    :reducer (fn [db msg]
               (assoc-in db [:requests (:key msg)] msg))}

   "igo-offer-match"
   {:schema {:gameTerms schemas/GameTerms
             :opponent st/string
             :opponentWhite st/boolean}
    :reducer (fn [db msg]
               db)}

   "igo-accept-match"
   {:schema {:messageKey schemas/MessageKey}
    :reducer (fn [db msg]
               (let [key (get-in msg [:content :messageKey])
                     game (get-in db [:games])]))}

   "igo-decline-match"
   {:schema {:messageKey schemas/MessageKey}
    :reducer (fn [db msg]
               db)}

   "igo-move"
   {:schema {:previousMove schemas/MessageKey
             :position schemas/Position}
    :reducer (fn [db msg]
               db)}

   "igo-chat"
   {:schema {:move schemas/MessageKey
             :text st/string}
    :reducer (fn [db msg]
               db)}
   }
)


(defn restructure-message
  "Get all necessary data into the message in a flatter, more convenient structure"
  [msg]
  ;; TODO: js->clj is slow, eventually we'll have to change this
  ;; (maybe mod scuttlebot to stream messages as raw JSON and parse as transit??)
  (let [key (.-key msg)
        value (.-value msg)
        author (.-author value)
        content (js->clj (.-content value) :keywordize-keys true)]
    {:content content
     :meta {:key key
            :author author}}))

(def igo-message-type?
  (set (keys message-protocol)))

(defn content-valid?
  [schema msg]
  (st/valid? (:content msg) schema))


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

(defn test-reducer
  "TODO: remove"
  [db msg]
  (update db :total-test inc))

(defn flume-reduce-fn
  [db msg]
  (let [type (get-in msg [:content :type])
        reducer (get-in message-protocol [type :reducer])]
   (-> db
       (reducer msg)
       (test-reducer msg) ;; TODO: remove
       )))

(defn flume-map-fn
  [msg]
  (when (igo-message-type? (.. msg -value -content -type))
    (let [msg (restructure-message msg)
          type (get-in msg [:content :type])
          schema (get-in message-protocol [type :schema])]
      (when (content-valid? schema msg)
        msg))
    ))

(def flume-view
  (flumeview-reduce
   index-version
   flume-reduce-fn
   flume-map-fn
   codec
   initial-index
   ))

(defn build-flume-view
  [sbot index-name]
  ; TODO: apparently this is temporary?
  (._flumeUse sbot
              (or index-name default-index-name)
              flume-view))

(defn view-mapper
  [f view cb]
  (.get view (fn [err val]
               (if err
                 (cb err nil)
                 (cb nil (f val))))))

(defn get-total
  [view cb]
  (view-mapper
   #(get-in % [:total-test])
   view
   cb
        ))
