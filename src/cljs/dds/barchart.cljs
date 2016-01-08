(ns dds.barchart
  (:require
   [schema.core :as s :include-macros true]
   [dds.c3 :as c3]))


(s/defn render :- js/Element
  [title :- s/Str
   x-domain :- [s/Str]
   heights :- [[s/Num]]
   series :- [s/Str]]
  (let [columns (mapv #(into [%1] %2) series heights)
       m {:data {:columns columns
                 :type "bar"}
          :axis {:x {:type "category"
                     :categories x-domain}}}]
   (c3/generate-element m)))
