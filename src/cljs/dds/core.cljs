(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]
   [dds.utils :as du]
   [dds.barchart :as bar]
   [dds.piechart :as pie]
   [dds.scatterplot :as splot]
   [dds.histogram :as hist]
   [dds.heatmap :as hmap]
   [dds.key-value-sequence :as kv]))

(s/defn ^:export ^:always-validate
  barchart :- js/Element
  [title :- s/Str
   x-domain :- [s/Str]
   heights :- [[s/Num]]
   series :- [s/Str]]
  (bar/render title x-domain heights series))

(s/defn ^:export ^:always-validate
  piechart :- js/Element
  [title :- s/Str
   category-counts :- [[(s/one s/Str "category") (s/one s/Num "count")]]]
  (pie/render title category-counts))

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
