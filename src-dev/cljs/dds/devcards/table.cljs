(ns dds.devcards.table
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Table"
  (dc/dom-node
   (fn [data-atom node]
     (let [schema [{:name "A" :nullable true :type "integer"}
                   {:name "B" :nullable true :type "string"}]
           content [[1 "B1"]
                    [2 "B2"]
                    [3 "B3"]]
           chart (dds/table "Table (A, B)" schema content)]
       (ddu/set-content! node chart)))))
