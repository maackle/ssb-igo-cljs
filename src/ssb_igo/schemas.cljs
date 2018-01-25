(ns ssb-igo.schemas
  (:require
   [struct.core :as s]))

(def GameTerms
  {:size s/integer
   :komi s/number
   :handicap s/integer})

(def Position
  {:row s/integer
   :col s/integer})
