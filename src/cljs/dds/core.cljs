(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]
   [dds.barchart :as bar]
   [dds.piechart :as pie]
   [dds.scatterplot :as scatter]
   [dds.histogram :as hist]
   [dds.heatmap :as hmap]
   [dds.key-value-sequence :as kvs]
   [dds.graph :as graph]
   [dds.table :as table]))

(s/defn ^:always-validate
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

(s/defn ^:always-validate
  piechart :- js/Element
  [title :- s/Str
   category-counts :- [[(s/one s/Str "category")
                        (s/one s/Num "count")]]]
  (let [container (du/create-div)
        title-div (du/create-title-div title)
        chart (pie/render category-counts)]
    (.appendChild container title-div)
    (.appendChild container chart)
    container))

(s/defn ^:always-validate
  scatterplot :- js/Element
  [title :- s/Str
   points :- [{(s/required-key "x") s/Any
               (s/required-key "y") s/Any}]
   x-numeric? :- s/Bool
   y-numeric? :- s/Bool
   jitter? :- s/Bool]
  {:pre [(every? #(= (count %) 2) points)
         ;; jitter is supported only for categorical axis
         (if jitter?
           (not (and x-numeric? y-numeric?))
           true)]}
  (let [js-points (clj->js points)
        xs (map #(.-x %) js-points)
        xf (if x-numeric?
             (scatter/numerical-axis-f xs 0.01)
             (scatter/categorical-axis-f xs))
        ys (map #(.-y %) js-points)
        yf (if y-numeric?
             (scatter/numerical-axis-f ys 0.02)
             (scatter/categorical-axis-f ys))
        container (du/create-div)
        title-div (du/create-title-div title)
        chart-div (du/create-div)
        render-fn #(scatter/render chart-div js-points x-numeric? y-numeric?
                                   jitter? xf yf)]
    (.appendChild container title-div)
    (.appendChild container chart-div)
    (du/observe-inserted! chart-div render-fn)
    (du/on-window-resize! render-fn)
    container))

(s/defn ^:always-validate
  histogram :- js/Element
  [title :- s/Str
   bins :- [s/Num]
   frequencies :- [s/Num]]
  {:pre [(or (and (even? (count bins))
                   (odd? (count frequencies)))
              (and (odd? (count bins))
                   (even? (count frequencies))))]}
  (let [container (du/create-div)
        title-div (du/create-title-div title)
        chart-div (du/create-div)
        bin-maps (->>
                  (partition 2 1 bins)
                  (mapv
                   (fn [freq [start end]]
                     {:y freq
                      :start start
                      :end end})
                   frequencies))
        render-fn #(hist/render chart-div bin-maps)]
    (.appendChild container title-div)
    (.appendChild container chart-div)
    (du/observe-inserted! chart-div render-fn)
    (du/on-window-resize! render-fn)
    container))

(s/defn ^:always-validate
  heatmap :- js/Element
  [title :- s/Str
   values :- [[s/Num]]
   row-names :- [s/Str]
   col-names :- [s/Str]
   color-zeroes :- [s/Num]]
  {:pre [(= (count values) (count row-names))
         (every? #(= (count %) (count col-names)) values)]}
  (let [container (du/create-div)
        title-div (du/create-title-div title)
        chart-div (du/create-div)
        render-fn #(hmap/render chart-div values row-names
                                col-names color-zeroes)]
    (du/observe-inserted! chart-div render-fn)
    (du/on-window-resize! render-fn)
    (.appendChild container title-div)
    (.appendChild container chart-div)
    container))

(def ^:export key-value-seq kvs/render)

(def ^:export graph graph/render)

(def ^:export table table/render)
