(ns dds.js
  (:require
   [dds.core :as dds]
   [dds.graph :as dg]))

(defn- transform-args [args]
  (map #(js->clj % :keywordize-keys true) args))

(defn ^:export barChart [& args]
  (apply dds/barchart (transform-args args)))

(defn ^:export pieChart [& args]
  (apply dds/piechart (transform-args args)))

(defn ^:export scatterPlot [& args]
  (apply dds/scatterplot (transform-args args)))

(defn ^:export histogram [& args]
  (apply dds/histogram (transform-args args)))

(defn ^:export heatmap [& args]
  (apply dds/heatmap (transform-args args)))

(defn ^:export keyValueSequence [& args]
  (apply dds/key-value-seq (transform-args args)))

(defn ^:export graph [& args]
  (apply dds/graph (transform-args args)))

(def ^:export graphUtils #js {"styleEdgeLines" dg/style-edge-lines
                              "styleEdgeLabels" dg/style-edge-labels
                              "styleNodeLabels" dg/style-node-labels})

(defn ^:export table [& args]
  (apply dds/table (transform-args args)))
