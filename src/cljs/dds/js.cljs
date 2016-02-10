(ns dds.js
  (:require
   [dds.core :as dds]))

(defn- transform-args [args]
  (map #(js->clj % :keywordize-keys true) args))

(defn ^:export barchart [& args]
  (apply dds/barchart (transform-args args)))

(defn ^:export piechart [& args]
  (apply dds/piechart (transform-args args)))

(defn ^:export scatterplot [& args]
  (apply dds/scatterplot (transform-args args)))

(defn ^:export histogram [& args]
  (apply dds/histogram (transform-args args)))

(defn ^:export heatmap [& args]
  (apply dds/heatmap (transform-args args)))

(defn ^:export key_value_sequence [& args]
  (apply dds/key-value-seq (transform-args args)))

(defn ^:export graph [& args]
  (apply dds/graph (transform-args args)))

(defn ^:export table [& args]
  (apply dds/table (transform-args args)))
