(ns dds.scatterplot
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]
   [dds.utils :refer [deep-merge-with]]))

(defn handle-categorical-x [xs]
  (let [categories (into #{} xs)
        column-map (into {} (map vector categories (range)))]
    {:data {:columns [(into ["x"] (map #(get column-map %) xs))]}
     :axis {:x {:type "category"
                :categories xs}}}))

(defn handle-categorical-y [ys]
  (let [distinct-categories (into #{} ys)
        column-map (into {} (map vector  distinct-categories (range)))
        formatter #(nth ys %)
        column (into ["y"] (map #(get column-map %) ys))]
    {:data {:columns [column]}
     :tooptip {:format {:value formatter}}
     :axis {:y {:tick {:values (vals column-map)
                       :format formatter}}}}))

(defn jitter [xs]
  (let [range-band (- (apply max xs) (apply min xs))]
    (map #(-> (js/Math.random 1)
             (- 0.5)
             (* range-band 0.4)
             (+ %))
         xs)))

(defn handle-numeric-x [xs jitter?]
  (let [xs (if jitter?
             (jitter xs)
             xs)]
    {:data {:columns [(into ["x"] xs)]}
     :axis {:x {:tick {:format #(.round js/d3 % 2)}}}}))

(defn handle-numeric-y [ys jitter?]
  (let [ys (if jitter?
             (jitter ys)
             ys)]
    {:data {:columns [(into ["y"] ys)]}
     :tooltip {:format {:value #(.round js/d3 % 2)}}}))

(s/defn render :- js/Element
  [points :- [[s/Any]]
   x-numeric? :- s/Bool
   y-numeric? :- s/Bool
   jitter? :- s/Bool]
  (let [xs (map first points)
        x-map (if x-numeric?
                (handle-numeric-x xs jitter?)
                (handle-categorical-x xs))
        ys  (map second points)
        y-map (if y-numeric?
                (handle-numeric-y ys jitter?)
                (handle-categorical-y ys))
        base-map {:data {:x "x"
                         :type "scatter"}}]
    (c3/generate-element (deep-merge-with concat base-map x-map y-map))))
