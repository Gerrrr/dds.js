(ns dds.piechart
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]
   [dds.utils :as du]))

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   category-counts :- [[(s/one s/Str "category")
                        (s/one s/Num "count")]]]
  (let [m {:data {:columns category-counts
                  :type "pie"}}
        chart (c3/generate-element m)]
    (du/wrap-with-title chart title)))
