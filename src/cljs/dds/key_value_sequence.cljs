(ns dds.key-value-sequence
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]))

(defn render-loop [container kvs]
  (set! (.-innerHTML container) "")
  (let [kv-lst (mapv
             (fn [{:keys [key val]}]
               [{:entry key :type "key"}
                {:entry val :type "value"}]) kvs)
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
     (.text #(aget % "entry"))
     (.attr "class" #(aget % "type")))))

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   kvs :- [{(s/required-key :key) s/Any
            (s/required-key :val) s/Any}]]
  (let [chart (du/create-div)
        render-fn #(render-loop chart kvs)
        container (du/wrap-with-title chart title)]
    (du/observe-inserted! container render-fn)
    container))
