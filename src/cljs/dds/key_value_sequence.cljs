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
  (let [container (du/create-div)
        chart-div (du/create-div)
        title-div (du/create-title-div title)
        render-fn #(render-loop chart-div kvs)]
    (du/observe-inserted! chart-div render-fn)
    (du/on-window-resize! render-fn)
    (.appendChild container title-div)
    (.appendChild container chart-div)
    container))
