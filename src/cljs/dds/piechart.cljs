(ns dds.piechart
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]))

(s/defn render :- js/Element
  [category-counts :- [[(s/one s/Str "category") (s/one s/Num "count")]]]
  (let [m {:data {:columns category-counts
                  :type "pie"}}]
    (c3/generate-element m)))
