(require '[ssb-igo.core :as c])
(require '[ssb-igo.demo :as d])

(def sbot
  (-> (js/require "scuttlebot")
                      (.use (clj->js {:init c/init})) ))

(sbot (clj->js (assoc d/sbot-config :key nil)))
