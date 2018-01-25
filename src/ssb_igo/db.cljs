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
  {:games {}
   :total-test 0})

(def message-protocol
  {"igo-request-match"
   {:schema {:gameTerms schemas/GameTerms}
    :mapper (fn [msg]
              (assoc (:content msg) :status :open))
    :reducer (fn [db msg]
               (assoc-in db [:games (:key msg)] msg))}

   "igo-offer-match"
   {:schema {:gameTerms schemas/GameTerms
             :opponent s/string
             :opponentWhite s/boolean}
    :reducer (fn [db msg]
               db)}

   "igo-accept-match"
   {:schema {:messageKey s/string}
    :reducer (fn [db msg]
               (let [key (get-in msg [:content :messageKey])
                     game (get-in db [:games])]))}

   "igo-decline-match"
   {:schema {:messageKey s/string}
    :reducer (fn [db msg]
               db)}

   "igo-move"
   {:schema {:previousMove s/string
             :position schemas/Position}
    :reducer (fn [db msg]
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
    {:content content
     :meta {:key key
            :author author}}))

(def igo-message-type?
  (set (keys message-protocol)))

(defn content-valid?
  [schema msg]
  (s/valid? (:content msg) schema))


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
  [db msg]
  (update db :total-test inc))

(defn flume-reduce-fn
  [db msg]
  (let [type (get-in msg [:content :type])
        reducer (get-in message-protocol [type :reducer])]
   (trace "incoming message" msg)
   (-> db
       (reducer msg)
       (test-reducer msg))))

(defn flume-map-fn
  [msg]
  (when (igo-message-type? (.. msg -value -content -type))
    (let [msg (flatten-message msg)
          type (get-in msg [:content :type])
          schema (get-in message-protocol [type :schema])]
      (when (content-valid? schema msg)
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
          (if err
            (trace "get-total ERROR " err)
            (do
              (trace "Total messages:" (:total-test index))
              (trace "Full index: " index))
            )
          )))
