(ns ssb-igo.playbooks
  (:require
   [ssb-igo.util :refer (trace)]
   [ssb-igo.messages :as msg]
   [ssb-igo.test :as test]
   )
  )

(def pull (js/require "pull-stream"))
(def pull-abortable (js/require "pull-abortable"))
(def ssb-client (js/require "ssb-client"))

(defonce abortables (atom []))

(defn abort-feeds
  []
  (println "aborting " (count @abortables) " feeds")
  (run! #(.abort %) @abortables)
  (reset! abortables [])
  )

(def WHOAMI (keyword (aget js/process.env "SSB_TEST_PERSONA")))
(def test-shs "GVZDyNf1TrZuGv3W5Dpef0vaITW1UqOUO3aWLNBp-7A=")
(def users
  {:alice "@vfBqbKHRpJhlx1rOWzGm11qusF9xDC9EUZ7FjuMGfzg=.ed25519"
   :bob "@shtCgcgaMr6QjFWnGwts6kTMKAkhn/pFN77QggbgoPg=.ed25519"})

(defn run-playbook
  [client playbook]
  (println "RUNNING PLAYBOOK")
  (let [playbook (volatile! playbook)
        abortable (pull-abortable)
        stream-done
        #(if %
           (println "STREAM ERROR: " %)
           (println "--- stream done ---"))

        send-current-play!
        (fn []
          (let [[who content _] (first @playbook)]
            (test/pub-message client content)))

        receive!
        (fn [msg]
          (if (empty? @playbook)
            (println "Playbook done!")
            (when-not (aget msg "sync")
              (let [play (first @playbook)]
               (vswap! playbook rest)
               (trace "got msg" msg)
               (trace "the play" play)
               ))))]

    (send-current-play!)

    (swap! abortables conj abortable)

    (pull (.createLogStream client #js {:live true})
          abortable
          (.drain pull receive! stream-done))))

(defn playbook1
  []
  (let [terms {:size 19
               :komi 5.5
               :handicap 0}]
    [
     ; Alice requests match
     [:alice
      (msg/igo-request-match terms)
      {}]
     ; Bob offers match
     [:bob
      (msg/igo-offer-match (:alice users) :black terms)
      {}]

     [:alice
      (msg/igo-request-match terms)
      {}]

     [:alice
      (msg/igo-request-match terms)
      {}]
     ; Alice accepts match as Black
     ; Bob moves (INVALID)
     ; Alice moves
     ; Bob moves
     ; ...
     ; Alice passes
     ; Bob passes
     ]))

(defn -main [& args]
  (let [handler (fn [err client]
                  (if err
                    (println "ERR!" err)
                    (run-playbook client (playbook1))))]
    (test/test-client (test/sbot-config test-shs) handler)))

(def main -main)
