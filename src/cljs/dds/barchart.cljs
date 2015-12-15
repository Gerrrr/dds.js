(ns dds.barchart
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]
   [dds.c3 :as c3]))


(s/defrecord BarChart
    [title :- s/Str
     x-domain :- [s/Str]
     heights :- [[s/Num]]
     series :- [s/Str]]
  ps/Renderable
  (render
   [_]
   (let [columns (mapv #(into [%1] %2) series heights)
         m {:data {:columns columns
                   :type "bar"}
            :axis {:x {:type "category"
                       :categories x-domain}}}]
     (c3/generate-element m))))
