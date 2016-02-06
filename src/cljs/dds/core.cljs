(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]
   [dds.barchart :as bar]
   [dds.piechart :as pie]
   [dds.scatterplot :as scatter]
   [dds.histogram :as histogram]
   [dds.heatmap :as heatmap]
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

(def ^:export scatterplot scatter/render)

(def ^:export histogram histogram/render)

(def ^:export heatmap heatmap/render)

(def ^:export key-value-seq kvs/render)

(def ^:export graph graph/render)

(def ^:export table table/render)
