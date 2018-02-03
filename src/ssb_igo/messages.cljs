(ns ssb-igo.messages
  (:require
   [struct.core :as st]
   [ssb-igo.schemas :as schemas]
   [ssb-igo.util :refer (trace)]))

(defn dissoc-in
  "From https://stackoverflow.com/a/26215960/612758"
  [m p]
  (if (get-in m p)
    (update-in m
               (drop-last p)
               dissoc (last p))
    m))

(def protocol
  {"igo-request-match"
   ; Only one request allowed per user
   {:schema (array-map
             :gameTerms [st/required schemas/GameTerms])
    :reducer (fn [db {:keys [meta content] :as msg}]
               (assoc-in db [:requests (:author meta)] msg))}

   "igo-expire-request"
   ; Takes no args because you only expire your own request
   ; and requests are keyed by author
   {:schema (array-map)
    :reducer (fn [db {:keys [meta content] :as msg}]
               (let [author (:author meta)
                     path [:requests author]
                     target (get-in db path)
                     ]
                 (if (= author (get-in target [:meta :author]))
                   (dissoc-in db path)
                   db)))}

   "igo-offer-match"
   {:schema (array-map
             :gameTerms [st/required schemas/GameTerms]
             :opponentKey [st/required schemas/PlayerKey]
             :opponentWhite [st/required st/boolean])
    :reducer (fn [db {:keys [meta content] :as msg}]
               (assoc-in db [:offers (:key meta)] msg))}

   "igo-withdraw-offer"
   {:schema (array-map
             :messageKey [st/required schemas/MessageKey])
    :reducer (fn [db {:keys [meta content] :as msg}]
               (let [path [:offers (:messageKey content)]
                     target (get-in db path)
                     author (:author meta)]
                 (if (= author (get-in target [:meta :author]))
                   (dissoc-in db path)
                   db)))}

   "igo-accept-match"
   {:schema (array-map
             :messageKey [st/required schemas/MessageKey])
    :reducer (fn [db {:keys [meta content] :as msg}]
               (let [key (:messageKey content)
                     offer (get-in db [:offers key])]
                 (if offer
                   (-> db
                       (assoc-in [:games key] offer)
                       (dissoc-in [:offers key]))
                   db)))}

   "igo-decline-match"
   {:schema (array-map
             :messageKey [st/required schemas/MessageKey])
    :reducer (fn [db {:keys [meta content] :as msg}]
               db)}

   "igo-move"
   {:schema (array-map
             :position [st/required schemas/Position]
             :previousMove [st/required schemas/MessageKey]
             :subjectiveMoveNum [st/required st/integer]
             )
    :reducer (fn [db {:keys [meta content] :as msg}]
               db)}

   "igo-chat"
   {:schema (array-map
             :move [st/required schemas/MessageKey]
             :text [st/required st/string])
    :reducer (fn [db {:keys [meta content] :as msg}]
               db)}
   })

(defn build-msg
  [type & vs]
  (let [schema (get-in protocol [type :schema])
        ks (map first schema)]
    (-> (zipmap ks vs)
        (assoc :type type)
        (st/validate! schema))))

#_(defn build-msg
  [type data]
  (-> data
      (assoc :type type)
      (st/validate! (get-in protocol [type :schema]))))
