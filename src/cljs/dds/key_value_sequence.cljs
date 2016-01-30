(ns dds.key-value-sequence)

(defn render [container kvs]
  (set! (.-innerHTML container) "")
  (let [kv-lst (mapv
             (fn [[k v]]
               [{:entry k :type "key"}
                {:entry v :type "value"}])  kvs)
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
     (.attr "class" #(.-type %)))))
