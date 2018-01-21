(ns ssb-igo.util)


(defn trace [msg x] (println (str "•§•§•  " msg "  •§•§•\n") x) x)
(defn tracer [x msg] (trace msg x))
