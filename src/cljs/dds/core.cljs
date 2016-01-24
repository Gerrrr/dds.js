(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]
   [dds.barchart :as bar]
   [dds.piechart :as pie]
   [dds.scatterplot :as splot]
   [dds.histogram :as hist]
   [dds.heatmap :as hmap]
   [dds.key-value-sequence :as kv]
   [dds.graph :as graph]
   [dds.table :as table]))

(s/defn ^:export ^:always-validate
  barchart :- js/Element
  [title :- s/Str
   x-domain :- [s/Str]
   heights :- [[s/Num]]
   series :- [s/Str]]
  (let [container (du/create-div)
        title-div (du/create-title-div title)
        chart (bar/render x-domain heights series)]
    (.appendChild container title-div)
    (.appendChild container chart)
    container))

(s/defn ^:export ^:always-validate
  piechart :- js/Element
  [title :- s/Str
   category-counts :- [[(s/one s/Str "category") (s/one s/Num "count")]]]
  (let [container (du/create-div)
        title-div (du/create-title-div title)
        chart (pie/render category-counts)]
    (.appendChild container title-div)
    (.appendChild container chart)
    container))

(s/defn ^:export ^:always-validate
  scatterplot :- js/Element
  [title :- s/Str
   points :- [[s/Any]]
   x-numeric? :- s/Bool
   y-numeric? :- s/Bool]
  {:pre [(every? #(= (count %) 2) points)]}
  (splot/render title points x-numeric? y-numeric?))

(s/defn ^:export ^:always-validate
  histogram :- js/Element
  [title :- s/Str
   bins :- [s/Num]
   frequencies :- [s/Num]]
  {:pre [(or (and (even? (count bins))
                   (odd? (count frequencies)))
              (and (odd? (count bins))
                   (even? (count frequencies))))]}
  (let [container (du/create-div)
        bin-maps (->>
                  (partition 2 1 bins)
                  (mapv
                   (fn [freq [start end]]
                     {:y freq
                      :start start
                      :end end})
                   frequencies))
        render-fn #(hist/render container title bin-maps)]
    (du/observe-inserted! container render-fn)
    (du/on-window-resize! render-fn)
    container))

(s/defn ^:export ^:always-validate
  heatmap :- js/Element
  [title :- s/Str
   values :- [[s/Num]]
   row-names :- [s/Str]
   col-names :- [s/Str]
   color-zeroes :- [s/Num]]
  {:pre [(= (count values) (count row-names))
         (every? #(= (count %) (count col-names)) values)]}
  (let [container (du/create-div)
        render-fn #(hmap/render container title values row-names
                                col-names color-zeroes)]
    (du/observe-inserted! container render-fn)
    (du/on-window-resize! render-fn)
    container))

(s/defn ^:export ^:always-validate
  key-value-sequence :- js/Element
  [title :- s/Str
   kvs :- [[(s/one s/Str "k") (s/one s/Str "v")]]]
   (let [container (du/create-div)
         render-fn #(kv/render container title kvs)]
     (du/observe-inserted! container render-fn)
     (du/on-window-resize! render-fn)
     container))

(s/defn ^:export ^:always-validate
  graph :- js/Element
  [title :- s/Str
   vertices :- [s/Str]
   edges :- [[(s/one s/Num "source") (s/one s/Num "target") (s/one s/Str "label")]]
   show-node-labels? :- s/Bool
   show-edge-labels? :- s/Bool
   show-directions? :- s/Bool]
  (let [nodes (map (fn [v] {:label v}) vertices)
        links (map
               (fn [[s t l]] {:source s :target t :label l})
               edges)
        container (du/create-div)
        force (->
               (.-layout js/d3)
               (.force))
        render-fn #(graph/render container force title nodes links
                                 show-node-labels? show-edge-labels?
                                 show-directions?)]
    (du/observe-inserted! container render-fn)
    (du/on-window-resize! render-fn)
    container))

(s/defn ^:export ^:always-validate
  table :- js/Element
  [title :- s/Str
   schema :- [{(s/required-key "name") s/Str
               (s/required-key "nullable?") s/Bool
               (s/required-key "type") (s/enum "number" "string")}]
   content :- [[s/Any]]]
  {:pre [(every? #(= (count %) (count schema)) content)]}
  (let [container (du/create-div)
        grid (du/create-div)
        pager (du/create-div)
        render-fn #(table/render container pager grid title schema content)]
    (.add (.-classList grid) "grid")
    (.add (.-classList pager) "pager")
    (.appendChild container pager)
    (.appendChild container grid)
    (du/observe-inserted! container render-fn)
    (du/on-window-resize! render-fn)
    container))
