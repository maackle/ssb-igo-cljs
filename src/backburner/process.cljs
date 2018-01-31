

(def child-process (js/require "child_process"))

(defn exec [cmd opt f] (.exec child-process cmd opt f))
(defn spawn [cmd opt]
  (let [p (.spawn child-process cmd opt)]
    (.on (.-stdout p) "data" #(println (js->clj %)))
    (.on p "close" #(println "SErver exiTED with code " %))
    p))

(defn runserver
  []
  (spawn "dev/sbot" #js["server"])
  #_(spawn "sbot" #js["server"
                    "--"
                    "--path /tmp/def"
                    "--caps.shs=$SBOT_SHS"
                    "--caps.sign=$SBOT_SIGN" ]))
