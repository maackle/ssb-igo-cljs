(ns ssb-igo.views)


(defn view-mapper
  [f view cb]
  (.get view (fn [err val]
               (if err
                 (cb err nil)
                 (cb nil (clj->js (f val)))))))

#_(defn get-total
  [view cb]
  (view-mapper
   #(get-in % [:total-test])
   view
   cb
        ))

(defn get-total
  [db]
  (get-in db [:total-test]))

(defn get-requests
  [db]
  (get db :requests))

(defn get-offers
  [db]
  (get db :offers))

(defn get-games
  [db]
  (get db :games))
