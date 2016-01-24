(ns dds.devcards.pie
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Pie chart"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/piechart "Pie chart title" [["a" 30] ["b" 30]])]
       (ddu/set-content! node chart)))))
