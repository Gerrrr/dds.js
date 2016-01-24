(ns dds.devcards.histogram
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Histogram"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/histogram "title" [1 3.25 5.5 7.75 12] [6 1 0 1])]
       (ddu/set-content! node chart)))))
