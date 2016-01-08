(ns dds.histogram
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.c3 :as c3]
   [dds.utils :as du]))

(def margins
  {:left-margin 50
   :bottom-margin 20
   :right-margin 10
   :top-margin 10})

(s/defn ^:always-validate render
  [container :- js/Element
   title :- s/Str
   bin-maps :- [{(s/required-key :start) s/Num
                (s/required-key :end) s/Num
                (s/required-key :y) s/Num}]]
  (set! (.-innerHTML container) "")
  (let [width (du/get-width container)
        height (du/get-height container)
        {:keys [left-margin right-margin
                top-margin bottom-margin]} margins
        width (- width left-margin right-margin)
        height (- height top-margin bottom-margin)
        svg (->
             (.select js/d3 container)
             (.classed "c3" true)
             (.append "svg")
             (.attr "width" (+ width left-margin right-margin))
             (.attr "height" (+ height top-margin bottom-margin))
             (.append "g")
             (.attr "transform" (str "translate(" left-margin "," top-margin ")")))
        x-domain (->
                  (.-scale js/d3)
                  (.linear)
                  (.range #js [0 width])
                  (.domain #js [(apply min (map :start bin-maps))
                                (apply max (map :end bin-maps))]))
        bins (map
               (fn [{:keys [start end y]
                     :as m}]
                 (let [scaled-start (x-domain start)]
                   (assoc m
                          :start scaled-start
                          :width (- (x-domain end) scaled-start)
                          :height (/ y (- end start)))))
               bin-maps)
        y-domain (->
                  (.-scale js/d3)
                  (.linear)
                  (.range #js [height 0])
                  (.domain #js [0 (apply max (map :height bins))]))
        bins (->>
              bins
              (map
               (fn [{:keys [height]
                     :as bin}]
                 (assoc bin
                        :height (y-domain height))))
              (clj->js))]
     (->
      (.selectAll svg ".bin")
      (.data  bins)
      (.enter)
      (.append "rect")
      (.attr "fill" "steelblue")
      (.attr "class" "bin")
      (.attr "x" #(.-start %))
      (.attr "width" #(dec (.-width %)))
      (.attr "y" #(.-height %))
      (.attr "height" #(- height (.-height %))))
     (->
      (.append svg "g")
      (.attr "class" "x axis")
      (.attr "transform" (str "translate(0," height ")"))
      (.call (->
              (.-svg js/d3)
              (.axis)
              (.scale x-domain)
              (.orient "bottom"))))
     (->
      (.append svg "g")
      (.attr "class" "y axis")
      (.call (->
              (.-svg js/d3)
              (.axis)
              (.scale y-domain)
              (.orient "left"))))))
