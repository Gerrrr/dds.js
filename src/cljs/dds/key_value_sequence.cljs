(ns dds.key-value-sequence
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]))

(defn render-loop [container kvs]
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

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   kvs :- [[(s/one s/Str "k") (s/one s/Str "v")]]]
  (let [container (du/create-div)
        chart-div (du/create-div)
        title-div (du/create-title-div title)
        render-fn #(render-loop chart-div kvs)]
    (du/observe-inserted! chart-div render-fn)
    (du/on-window-resize! render-fn)
    (.appendChild container title-div)
    (.appendChild container chart-div)
    container))
