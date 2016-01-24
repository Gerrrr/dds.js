(ns dds.devcards.graph
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))

(defcard "## Graph with node labels, edge labels and directions"
  (dc/dom-node
   (fn [data-atom node]
     (let [graph (dds/graph "title" ["a" "b" "c"]
                            [[0 2 "a-c"]] true true true)]
       (ddu/set-content! node graph)))))

(defcard "## Graph with node labels and edge labels"
  (dc/dom-node
   (fn [data-atom node]
     (let [graph (dds/graph "title" ["a" "b" "c"]
                            [[0 2 "a-c"]] true true false)]
       (ddu/set-content! node graph)))))

(defcard "## Graph with node labels"
  (dc/dom-node
   (fn [data-atom node]
     (let [graph (dds/graph "title" ["a" "b" "c"]
                            [[0 2 "a-c"]] true false false)]
       (ddu/set-content! node graph)))))

(defcard "## Graph without any labels"
  (dc/dom-node
   (fn [data-atom node]
     (let [graph (dds/graph "title" ["a" "b" "c"]
                            [[0 2 "a-c"]] false false false)]
       (ddu/set-content! node graph)))))
