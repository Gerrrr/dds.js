(ns dds.devcards.scatter
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.utils :as ddu]
   [dds.core :as dds])
  (:require-macros
   [devcards.core :refer [defcard]]))


(defcard "## Scatter plot with numeric axes"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 1"
                  [{:x 1 :y 10} {:x 2 :y 12} {:x 3 :y 14}
                   {:x 3 :y 18} {:x 5 :y 33} {:x 6 :y 5}]
                  true true false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with categorical y axis"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 2"
                  [{:x 1 :y "a"} {:x 2 :y "b"} {:x 3 :y "a"}
                   {:x 5 :y "c"} {:x 6 :y "b"} {:x 3 :y "c"}]
                  true false false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with categorical y axis and jitter"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 3"
                  [{:x 1 :y "a"} {:x 2 :y "b"} {:x 3 :y "a"}
                   {:x 5 :y "c"} {:x 6 :y "b"} {:x 3 :y "a"}]
                  true false true)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with categorical x axis"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 4"
                  [{:x "a" :y 1} {:x "b" :y 2} {:x "c" :y 3}
                   {:x "a" :y 3} {:x "c" :y 5} {:x "b" :y 6}]
                  false true false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with categorical x axis and jitter"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 5"
                  [{:x "a" :y 1} {:x "b" :y 2} {:x "c" :y 3}
                   {:x "a" :y 3} {:x "c" :y 3} {:x "b" :y 2}]
                  false true true)]
       (ddu/set-content! node chart)))))



(defcard "## Scatter plot with both categorical axes"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 6"
                  [{:x "a" :y "cat1"} {:x "b" :y "cat2"} {:x "a" :y "cat3"}
                   {:x "b" :y "cat1"} {:x "c" :y "cat3"} {:x "a" :y "cat3"}]
                  false false false)]
       (ddu/set-content! node chart)))))

(defcard "## Scatter plot with both categorical axes and jitter"
  (dc/dom-node
   (fn [data-atom node]
     (let [chart (dds/scatterplot
                  "scatter 7"
                  [{:x "a" :y "cat1"} {:x "b" :y "cat2"} {:x "a" :y "cat3"}
                   {:x "b" :y "cat1"} {:x "c" :y "cat3"} {:x "a" :y "cat3"}]
                  false false false)]
       (ddu/set-content! node chart)))))
