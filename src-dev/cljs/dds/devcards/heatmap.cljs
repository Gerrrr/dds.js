(ns dds.devcards.heatmap
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Heatmap"
  (dc/dom-node
   (fn [data-atom node]
     (let [matrix [[1 2 3] [1 2 1] [4 5 6] [7 2 1]]
           col-names ["A" "B" "C"]
           row-names ["1" "2" "3" "4"]
           chart (dds/heatmap "title" matrix row-names col-names [0 10])]
       (ddu/set-content! node chart)))))

(defcard "## Heatmap with nulls"
  (dc/dom-node
   (fn [data-atom node]
     (let [matrix [[1 2] [3 nil]]
           col-names ["A" "B"]
           row-names ["1" "2"]
           chart (dds/heatmap "title" matrix row-names col-names [1 3])]
       (ddu/set-content! node chart)))))
