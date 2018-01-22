(ns ssb-igo.util
  (:require
   [cognitect.transit :as transit]
   ))


(defn trace [msg x] (println (str "•§•§•  " msg "  •§•§•\n") x) x)
(defn tracer [x msg] (trace msg x))

; Transit
(def tr (transit/reader :json))
(def tw (transit/writer :json))
(defn t-read [x] (transit/read tr x))
(defn t-write [x] (transit/write tw x))
