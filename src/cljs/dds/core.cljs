(ns dds.core
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]
   [dds.barchart :refer [BarChart]]
   [dds.piechart :refer [PieChart]]
   [dds.scatterplot :refer [ScatterPlot]]
   [dds.histogram :refer [Histogram]]
   [dds.heatmap :refer [Heatmap]]))

(defn ^:export barchart
  [title x-domain heights series]
  (ps/render (BarChart. title x-domain heights series)))

(defn ^:export piechart
  [title category-counts]
  (ps/render (PieChart. title category-counts)))

(defn ^:export scatterplot
  [title points x-numeric? y-numeric?]
  (ps/render (ScatterPlot. title points x-numeric? y-numeric?)))

(defn ^:export histogram
  [title  bins frequencies]
  (ps/render (Histogram. title bins frequencies)))

(defn ^:export heatmap
  [title values row-names col-names color-zeroes]
  (ps/render (Heatmap. title values row-names col-names color-zeroes)))
