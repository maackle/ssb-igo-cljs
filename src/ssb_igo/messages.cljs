(ns ssb-igo.messages
  (:require
   [struct.core :as st]
   [ssb-igo.schemas :as schemas]
   [ssb-igo.util :refer (trace)]))

(defn validate
  [content]
  (let [type (-> content :type keyword)
        schema (get schemas/MessageTypes type)]
    (st/validate! content schema)))

(defn igo-request-match
  [gameTerms]
  (validate
   {:type "igo-request-match"
    :gameTerms gameTerms}))

(defn igo-offer-match
  [opponent opponentColor gameTerms]
  (validate
   {:type "igo-offer-match"
    :opponent opponent
    :opponentColor opponentColor
    :gameTerms gameTerms}))

(defn igo-move
  [position moveNum prevMove]
  (validate
   {:type "igo-move"
    :position position
    :moveNum moveNum
    :prevMove prevMove}))

(defn igo-chat
  [text moveKey]
  (validate
   {:type "igo-chat"
    :text text
    :move moveKey}))
