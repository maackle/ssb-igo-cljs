(ns ssb-igo.test
  (:require ssb-igo.core))

(def tape (js/require "tape"))
(def Playbook (-> (js/require "scuttle-playbook")
                  (.use (.-plugin ssb-igo.core))))

(tape
 "it works"
 (fn [t]
   (Playbook
    (fn [sbot]
      (fn [alice bob]
        (println "alice" alice)
        (let [api (.-ssbIgoDb sbot)]
          (clj->js [[alice {:type "igo-chat"
                             :move "foo"
                             :text "yo! bobbo!"}]
                     (fn [done]
                       (.getTotal api (fn [_ v] (.equal t v 1) (done))))
                     ]))))
    (.end t))
   ))
