(ns dds.devcards.scatter
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))


(defcard "## Scatter plot with numeric axes but without jitter"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter without jitter"
                  [[1 10] [2 12] [3 14] [3 18] [5 33] [6 5]]
                  true true false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with numeric axes and jitter"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter with jitter"
                  [[1 10] [2 12] [3 14] [3 18] [5 33] [6 5]]
                  true true true)]
       (ddu/set-content! node chart)))))



(defcard "## Scatter plot with y axis categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 2"
                  [[1 "a"] [2 "b"] [3 "c"] [3 "a"] [5 "c"] [6 "b"]]
                  true false false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with x axes categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 3"
                  [["a" 1] ["b" 2] ["c" 3] ["a" 3] ["c" 5] ["b" 6]]
                  false true false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with both axes categorized"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 4"
                  [["a" "cat1"] ["b" "cat2"] ["a" "cat3"]
                   ["b" "cat1"] ["c" "cat3"]]
                  false false false)]
       (ddu/set-content! node chart)))))
