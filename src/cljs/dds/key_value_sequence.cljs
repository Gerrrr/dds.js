(ns dds.key-value-sequence
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]
   [dds.utils :as du]))

(defn render [container title kvs]
  (set! (.-innerHTML container) "")
  (let [kv-lst (mapv
             (fn [[k v]]
               [{:entry k :class "key"}
                {:entry v :class "value"}])  kvs)
        table (->
               (.select js/d3 container)
               (.classed "c3" true)
               (.append "table")
               (.attr "display" "table-cell")
               (.attr "verticalAlign" "middle")
               (.attr "textAlign" "center")
               (.classed "keyValueTable" true))
        rows (->
              (.selectAll table "tr")
              (.data (clj->js kv-lst))
              (.enter)
              (.append "tr"))]
    (->
     (.selectAll rows "td")
     (.data identity)
     (.enter)
     (.append "td")
     (.text #(.-entry %))
     (.attr "class" #(.-class %)))))
