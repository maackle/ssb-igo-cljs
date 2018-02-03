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

(def PlayerKey st/string)

#_(def Player
  {:key PlayerKey})


(def Game
  {:gameTerms GameTerms
   :players [Player] ; [black, white]
   :status (one-of [:open :active :closed])
   })

(def FlumeIndex
  {:games {st/string st/map}
   :offers {st/string st/map}
   })
