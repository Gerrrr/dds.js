(ns dds.scatterplot
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]
   [dds.utils :refer [deep-merge-with]]))

(defn categorize-x [xs]
  (let [categories (into #{} xs)
        column-map (into {} (map vector categories (range)))]
    {:data {:columns [(into ["x"] (map #(get column-map %) xs))]}
     :axis {:x {:type "category"
                :categories xs}}}))

(defn categorize-y [ys]
  (let [distinct-categories (into #{} ys)
        column-map (into {} (map vector  distinct-categories (range)))
        formatter #(nth ys %)
        column (into ["y"] (map #(get column-map %) ys))]
    {:data {:columns [column]}
     :tooptip {:format {:value formatter}}
     :axis {:y {:tick {:values (vals column-map)
                       :format formatter}}}}))

(s/defn render :- js/Element
  [title :- s/Str
   points :- [[s/Any]]
   x-numeric? :- s/Bool
   y-numeric? :- s/Bool]
  (let [xs (map first points)
        x-map (if x-numeric?
                {:data {:columns [(into ["x"] xs)]}}
                (categorize-x xs))
        ys (map second points)
        y-map (if y-numeric?
                {:data {:columns [(into ["y"] ys)]}}
                (categorize-y ys))
        base-map {:title {:text title}
                  :data {:x "x"
                         :type "scatter"}}]
    (c3/generate-element (deep-merge-with concat base-map x-map y-map))))
