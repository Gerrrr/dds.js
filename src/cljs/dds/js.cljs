(ns dds.js
  (:require
   [dds.core :as dds]))

(defn ^:export barchart [& args]
  (apply dds/barchart (map js->clj args)))

(defn ^:export piechart [& args]
  (apply dds/piechart (map js->clj args)))

(defn ^:export scatterplot [& args]
  (apply dds/scatterplot (map js->clj args)))

(defn ^:export histogram [& args]
  (apply dds/histogram (map js->clj args)))

(defn ^:export heatmap [& args]
  (apply dds/heatmap (map js->clj args)))

(defn ^:export key_value_sequence [& args]
  (apply dds/key-value-seq (map js->clj args)))

(defn ^:export graph [& args]
  (apply dds/graph (map js->clj args)))

(defn ^:export table [& args]
  (apply dds/graph (map js->clj args)))
