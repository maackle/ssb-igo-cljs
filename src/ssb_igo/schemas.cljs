(ns ssb-igo.schemas
  (:require
   [struct.core :as s]))

(defn one-of
  [vals]
  {:message (str "Must be one of vals:" vals)
   :optional false
   :validate #(contains? vals %)})

(def GameTerms
  {:size s/integer
   :komi s/number
   :handicap s/integer})

(def Position
  {:row s/integer
   :col s/integer})

(def Player
  {:key s/string})

(def Game
  {:gameTerms GameTerms
   :players [Player] ; [black, white]
   :status (one-of [:open :active :closed])
   })

(def FlumeIndex
  {:games {s/string s/map}
   :offers {s/string s/map}
   })
