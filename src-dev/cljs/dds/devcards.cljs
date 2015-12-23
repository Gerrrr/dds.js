(ns dds.devcards
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard defcard-doc]]))

(enable-console-print!)

(devcards.core/start-devcard-ui!)

(defn- set-content! [node html-obj]
  (set! (.-innerHTML node) "")
  (.appendChild node html-obj))

(defcard  "## Pie chart"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/piechart "title" [["a" 30] ["b" 30]])]
       (set-content! node chart)))))

(defcard "## Bar chart"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/barchart
                  "title"
                  ["a" "b" "c"]
                  [[3 2 1] [5 6 4]] ["x" "y"])]
       (set-content! node chart)))))

(defcard "## Scatter plot with numeric axes"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "title"
                  [[1 10] [2 12] [3 14] [3 18] [5 33] [6 5]]
                  true true)]
       (set-content! node chart)))))

(defcard "## Scatter plot with y axis categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "title"
                  [[1 "a"] [2 "b"] [3 "c"] [3 "a"] [5 "c"] [6 "b"]]
                  true false)]
       (set-content! node chart)))))

(defcard "## Scatter plot with x axes categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "title"
                  [["a" 1] ["b" 2] ["c" 3] ["a" 3] ["c" 5] ["b" 6]]
                  false true)]
       (set-content! node chart)))))

(defcard "## Scatter plot with both axes categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "title"
                  [["a" "cat1"] ["b" "cat2"] ["a" "cat3"] ["b" "cat1"] ["c" "cat3"]]
                  false false)]
       (set-content! node chart)))))


(defcard "## Histogram"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/histogram "title" [1 3.25 5.5 7.75 12] [6 1 0 1])]
       (set-content! node chart)))))



(defcard "## Heatmap"
  (dc/dom-node
   (fn [data-atom node]
     (let [matrix [[1 2 3] [1 2 1] [4 5 6] [7 2 1]]
           col-names ["A" "B" "C"]
           row-names ["1" "2" "3" "4"]
           chart (dds/heatmap "title" matrix row-names col-names [0 10])]
       (set-content! node chart)))))

(defcard "## Key Value Sequence"
  (dc/dom-node
   (fn [data-atom node]
     (let [seq [["hello" "world"] ["foo" "bar"] ["a" "b"]]
           chart (dds/key-value-sequence "title" seq)]
       (set-content! node chart)))))
