(ns ssb-igo.schemas
  (:require
   [schema.core :as s]))

(def GameTerms
  {:size s/Int
   :komi s/Num
   :handicap s/Int})

(def Position
  {:row s/Int
   :col s/Int})
