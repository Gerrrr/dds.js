(ns dds.heatmap
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.utils :as du]))

(def margins
  {:left-margin 50
   :bottom-margin 20
   :right-margin 10
   :top-margin 10})

(s/defn ^:always-validate render-loop
  [container :- js/Element
   values :- [[s/Num]]
   row-names :- [s/Str]
   col-names :- [s/Str]
   color-zeroes :- [s/Num]]
  (set! (.-innerHTML container) "")
  (let [{:keys [left-margin right-margin
                top-margin bottom-margin]} margins
        width (- (du/get-width container) left-margin right-margin)
        height (- (du/get-height container) top-margin bottom-margin)
        x (->
           (.-scale js/d3)
           (.ordinal)
           (.domain (clj->js col-names))
           (.rangeBands #js [0 width]))
        y (->
           (.-scale js/d3)
           (.ordinal)
           (.domain (clj->js row-names))
           (.rangeBands #js [height 0]))
        z-domain #js [(first color-zeroes) (last color-zeroes)]
        z-scale-string (case (count color-zeroes)
                         2 "YlOrRd"
                         3 "PRGn"
                         (throw
                          (js/Error.
                           (str
                            "Currently only up to three colors are supported, but given: "
                            color-zeroes))))
        z (->
           (.scale js/chroma z-scale-string)
           (.domain z-domain))
        x-axis (->
                (.-svg js/d3)
                (.axis)
                (.scale x)
                (.orient "bottom"))
        y-axis (->
                (.-svg js/d3)
                (.axis)
                (.scale y)
                (.orient "left"))
        indexed-values (->>
                        values
                        (map-indexed
                         (fn [i row]
                           (map-indexed
                            (fn [j val]
                              {:x j :y i :val val})
                            row)))
                        (flatten)
                        (clj->js))
        chart (->
               (.select js/d3 container)
               (.classed "c3" true)
               (.append "svg:svg")
               (.attr "width" (+ width left-margin right-margin))
               (.attr "height" (+ height top-margin bottom-margin))
               (.append "g")
               (.attr "transform" (str "translate(" left-margin "," top-margin ")"))
               (.attr "width" width)
               (.attr "height" height)
               (.attr "class" "main"))]
    (->
     (.append chart "g")
     (.attr "transform" (str "translate(0," height ")"))
     (.attr "class" "x axis")
     (.call x-axis))

    (->
     (.append chart "g")
     (.attr "transform" "translate(0,0)")
     (.attr "class" "y axis")
     (.call y-axis))

    (->
     (.append chart "svg:g")
     (.selectAll "matrix-rects")
     (.data indexed-values)
     (.enter)
     (.append "rect")
     (.attr "class" "cell")
     (.attr "x" #(->>
                  (aget % "x")
                  (p/safe-get col-names)
                  (x)
                  (inc)))
     (.attr "y" #(->>
                  (aget % "y")
                  (p/safe-get row-names)
                  (y)))
     (.attr "width" (dec (.rangeBand x)))
     (.attr "height" (dec (.rangeBand y)))
     (.attr "fill" #(if-let [val (aget % "val")]
                      (z val)
                      "#000000"))
     (.attr "class" "matrix-cell")
     (.append "svg-title")
     (.text #(aget % "val")))))

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   values :- [[s/Num]]
   row-names :- [s/Str]
   col-names :- [s/Str]
   color-zeroes :- [s/Num]]
  {:pre [(= (count values) (count row-names))
         (every? #(= (count %) (count col-names)) values)]}
  (let [container (du/create-div)
        chart (du/create-div)
        render-fn #(render-loop chart values row-names
                                col-names color-zeroes)
        container (du/wrap-with-title chart title)]
    (du/observe-inserted! container render-fn)
    container))
