(ns dds.devcards.bar
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Bar chart"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/barchart
                  "Bar chart title"
                  ["a" "b" "c"]
                  [[3 2 1] [5 6 4]] ["x" "y"])]
       (ddu/set-content! node chart)))))
