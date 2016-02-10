(ns dds.devcards.kvs
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Key Value Sequence"
  (dc/dom-node
   (fn [data-atom node]
     (let [seq [{:key "hello" :val "world"}
                {:key "foo" :val "bar"}
                {:key "a" :val "b"}]
           chart (dds/key-value-seq "title" seq)]
       (ddu/set-content! node chart)))))
