(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]
   [dds.utils :as du]
   [dds.barchart :refer [BarChart]]
   [dds.piechart :refer [PieChart]]
   [dds.scatterplot :refer [ScatterPlot]]
   [dds.histogram :as hist]
   [dds.heatmap :as hmap]
   [dds.key-value-sequence :as kv]))

(defn ^:export barchart
  [title x-domain heights series]
  (ps/render (BarChart. title x-domain heights series)))

(defn ^:export piechart
  [title category-counts]
  (ps/render (PieChart. title category-counts)))

(defn ^:export scatterplot
  [title points x-numeric? y-numeric?]
  (ps/render (ScatterPlot. title points x-numeric? y-numeric?)))

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
