(ns ssb-igo.schemas
  (:require
   [struct.core :as st]))

(defn one-of
  [vals]
  {:message (str "Must be one of vals:" vals)
   :optional false
   :validate #(contains? vals %)})

(def GameTerms
  {:size st/integer
   :komi st/number
   :handicap st/integer})

(def Position
  {:row st/integer
   :col st/integer})

(def MessageKey st/string)

(def Player
  {:key st/string})

(def Game
  {:gameTerms GameTerms
   :players [Player] ; [black, white]
   :status (one-of [:open :active :closed])
   })

(def FlumeIndex
  {:games {st/string st/map}
   :offers {st/string st/map}
   })

(def MessageTypes
  {:igo-request-match
   {:gameTerms GameTerms}

   :igo-offer-match
   {:opponent Player}

   :igo-accept-match
   {:target MessageKey}

   :igo-decline-match
   {:target MessageKey}

   :igo-move
   {:position Position
    :prevMove MessageKey
    :moveNum st/integer}

   :igo-chat
   {:text st/string
    :move MessageKey}})
