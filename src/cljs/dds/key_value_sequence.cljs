(ns dds.key-value-sequence
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.c3 :as c3]
   [dds.protocols :as ps]
   [dds.utils :as du]))

(defn get-margins [node]
  {:left-margin 0
   :bottom-margin 0
   :right-margin 0
   :top-margin 0})


(s/defn render-kv [container title kvs]
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

(s/defrecord KeyValueSequence
    [title :- s/Str
     kvs :- [[s/Str s/Str]]]
  ps/Renderable
  (render
   [_]
   (let [container (du/create-div)
         render-fn #(render-kv container title kvs)
         observer (du/create-mutation-observer render-fn)]
     (set! (.-onresize js/window) render-fn)
     (set! (.-id container) "hhh")
     (.observe observer js/document #js {"attributes" true
                                         "childList" true
                                         "characterData" true
                                         "subtree" true})
     container)))
