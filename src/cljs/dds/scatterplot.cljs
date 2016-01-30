(ns dds.scatterplot
  (:require
   [schema.core :as s :include-macros true]
   [dds.utils :as du]))

(defn numerical-axis-f [xs diff-scale]
  (let [min-x (apply min xs)
        max-x (apply max xs)
        dx (- max-x min-x)
        f (-> (.-scale js/d3)
              (.linear)
              (.domain #js [(- min-x (* diff-scale dx))
                            (+ max-x (* diff-scale dx))]))]
    (fn [start stop]
      (.range f #js [start stop]))))

(defn categorical-axis-f [xs]
  (let [unique-xs (-> (into #{} xs)
                      (clj->js))
        f (-> (.-scale js/d3)
              (.ordinal)
              (.domain unique-xs))]
    (fn [start stop]
      (.rangeBands f #js [start stop]))))

(defn axis-value [jitter? numeric? axis-f property-f p]
  (let [val (property-f p)]
    (if numeric?
      (axis-f val)
      (let [jitter (if jitter?
                     (-> (js/Math.random 1)
                         (- 0.5)
                         (* (.rangeBand axis-f) 0.4))
                     0)]
        (+ (axis-f val)
           (/ (.rangeBand axis-f) 2)
           jitter)))))

(def margins
  {:left-margin 30
   :bottom-margin 20
   :right-margin 10
   :top-margin 10})

(s/defn render :- js/Element
  [container :- js/Element
   points :- [[s/Any]]
   x-numeric? :- s/Bool
   y-numeric? :- s/Bool
   jitter? :- s/Bool
   xf
   yf]
  (set! (.-innerHTML container) "")
  (let [{:keys [left-margin right-margin
                top-margin bottom-margin]} margins
        width (- (du/get-width container) left-margin right-margin)
        height (- (du/get-height container) top-margin bottom-margin)
        xf (xf 0 width)
        yf (yf height 0)
        chart (->
               (.select js/d3 container)
               (.append "svg:svg")
               (.attr "width" (+ width right-margin left-margin))
               (.attr "height" (+ height top-margin bottom-margin))
               (.attr "class" "c3"))
        main (->
              (.append chart "g")
              (.attr "transform" (str "translate(" left-margin
                                      "," top-margin ")"))
              (.attr "width" width)
              (.attr "height" height)
              (.attr "class" "main"))
        x-axis (->
                (.-svg js/d3)
                (.axis)
                (.scale xf)
                (.orient "bottom"))
        y-axis (->
                (.-svg js/d3)
                (.axis)
                (.scale yf)
                (.orient "left"))]
    (->
     (.append main "g")
     (.attr "transform" (str "translate(0," height ")"))
     (.attr "class" "x axis")
     (.call x-axis))

    (->
     (.append main "g")
     (.attr "transform" "translate(0,0)")
     (.attr "class" "y axis")
     (.call y-axis))

    (->
     (.append main "svg:g")
     (.selectAll "scatter-dots")
     (.data points)
     (.enter)
     (.append "svg:circle")
     (.attr "cx" (partial axis-value jitter? x-numeric? xf #(.-x %)))
     (.attr "cy" (partial axis-value jitter? y-numeric? yf #(.-y %)))
     (.attr "r" 3))))
