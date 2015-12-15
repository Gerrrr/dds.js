(ns dds.piechart
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]
   [dds.c3 :as c3]))

(s/defrecord PieChart
    [title :- s/Str
     category-counts :- [[s/Str s/Num]]]
  ps/Renderable
  (render
   [_]
   (let [m {:data {:columns category-counts
                   :type "pie"}}]
     (c3/generate-element m))))
