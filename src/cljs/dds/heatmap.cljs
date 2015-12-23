(ns dds.heatmap
  (:require
   [cljs.pprint :refer [pprint]]
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.protocols :as ps]
   [dds.utils :as du]))

(defn get-margins [node]
  {:left-margin 50
   :bottom-margin 20
   :right-margin 10
   :top-margin 10})

(defn render-heatmap [container values row-names col-names color-zeroes]
  (set! (.-innerHTML container) "")
  (let [{:keys [left-margin right-margin
                top-margin bottom-margin]
         :as m} (get-margins container)
        width (- (du/get-width container) left-margin right-margin)
        height (- (du/get-height container) top-margin bottom-margin)
        x (->
           (.-scale js/d3)
           (.ordinal)
           (.domain (clj->js col-names))
           (.rangeBands (clj->js [0 width])))
        y (->
           (.-scale js/d3)
           (.ordinal)
           (.domain (clj->js row-names))
           (.rangeBands (clj->js [height 0])))
        z-domain [(first color-zeroes) (last color-zeroes)]
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
           (.domain (clj->js z-domain)))
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
                        (flatten))
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
     (.data (clj->js indexed-values))
     (.enter)
     (.append "rect")
     (.attr "class" "cell")
     (.attr "x" #(->>
                  (.-x %)
                  (p/safe-get col-names)
                  (x)
                  (inc)))
     (.attr "y" #(->>
                  (.-y %)
                  (p/safe-get row-names)
                  (y)))
     (.attr "width" (dec (.rangeBand x)))
     (.attr "height" (dec (.rangeBand y)))
     (.attr "fill" #(if-let [val (.-val %)]
                      (z val)
                      "#000000"))
     (.attr "class" "matrix-cell")
     (.append "svg-title")
     (.text #(.-val %)))))

(s/defrecord Heatmap
    [title :- s/Str
     values :- [[s/Num]]
     row-names :- [s/Str]
     col-names :- [s/Str]
     color-zeroes :- [s/Num]]
  ps/Renderable
  (render
   [_]
   (let [container (du/create-div)
         render-fn #(render-heatmap container values row-names
                                    col-names color-zeroes)
         observer (js/MutationObserver.
                   (fn [mutations]
                     (doseq [mutation mutations]
                       (when (and (= (.-type mutation) "childList")
                                  (.-previousSibling mutation))
                         (this-as this
                                  (.disconnect this))
                         (js/setTimeout render-fn 10)))))]
     (set! (.-onresize js/window) render-fn)
     (.observe observer js/document #js {"attributes" true
                                         "childList" true
                                         "characterData" true
                                         "subtree" true})
     container)))
