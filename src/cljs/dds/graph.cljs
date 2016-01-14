(ns dds.graph
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.c3 :as c3]
   [dds.utils :as du]))

(def margins
  {:left-margin 0
   :bottom-margin 0
   :right-margin 0
   :top-margin 0})

(s/defn ^:always-validate render
  [container :- js/Element
   force :- js/Object
   title :- s/Str
   nodes :- [{(s/required-key :label) s/Str}]
   links :- [{(s/required-key :source) s/Num
               (s/required-key :target) s/Num
               (s/required-key :label) s/Str}]
   show-node-labels? :- s/Bool
   show-edge-labels? :- s/Bool
   show-directions?] :- s/Bool
   (set! (.-innerHTML container) "")
   (let [links (clj->js links)
         nodes (clj->js nodes)
         {:keys [left-margin right-margin
                 top-margin bottom-margin]
          :as m} margins
         width (- (du/get-width container) left-margin right-margin)
         height (- (du/get-height container) top-margin bottom-margin)
         svg (->
              (.select js/d3 container)
              (.append "svg")
              (.attr "width" width)
              (.attr "height" height))
         _    (->
               (.append svg "svg:marker")
               (.attr "id" "triangle")
               (.attr "viewBox" "0 0 10 10")
               (.attr "refX" 16)
               (.attr "refY" 5)
               (.attr "markerUnits" "strokeWidth")
               (.attr "markerWidth" 7)
               (.attr "markerHeight" 7)
               (.attr "orient" "auto")
               (.attr "class" "arrowHead")
               (.append "svg:path")
               (.attr "d" "M 0 0 L 10 5 L 0 10 z"))
         force (->
                force
                (.size #js [width height])
                (.nodes nodes)
                (.links links)
                (.linkDistance (/ (min width height) 6.5))
                (.charge -500))
         links (->
                (.selectAll svg ".link")
                (.data links)
                (.enter))
         link-lines (->
                     (.append links "line")
                     (.attr "class" "link")
                     (.attr "marker-end" (if show-directions?
                                           "url(#triangle)"
                                           "")))
         link-labels (->
                      (.append links "text")
                      (.text #(.-label %))
                      (.attr "fill" "black")
                      (.attr "class" "edgeLabel")
                      (.attr "text-anchor" "middle")
                      (.style "visibility" (if show-edge-labels?
                                             "visible"
                                             "hidden")))
         nodes (->
                (.selectAll svg ".node")
                (.data nodes)
                (.enter))
         circles (->
                  (.append nodes "circle")
                  (.attr "class" "node")
                  (.call (.-drag force)))
         node-labels (->
                      (.append nodes "text")
                      (.text #(.-label %))
                      (.attr "fill" "black")
                      (.attr "class" "nodeLabel")
                      (.style "visibility" (if show-node-labels?
                                             "visible"
                                             "hidden")))]
     (.on force
          "tick"
          (fn []
            (->
             (.attr circles "r" 5)
             (.attr "cx" #(.-x %))
             (.attr "cy" #(.-y %)))

            (->
             node-labels
             (.attr "x" #(+ (.-x %) 7))
             (.attr "y" #(- (.-y %) 4)))

            (->
             link-labels
             (.attr "x" #(if (> (aget % "target" "x")
                                (aget % "source" "x"))
                           (+ (aget % "source" "x")
                              (/ (- (aget % "target" "x")
                                    (aget % "source" "x"))
                                 2))
                           (+ (aget % "target" "x")
                              (/ (- (aget % "target" "x")
                                    (aget % "source" "x"))
                                 2))))
             (.attr "y" #(if (> (aget % "target" "y")
                                (aget % "source" "y"))
                           (+ (aget % "source" "y")
                              (/ (- (aget % "target" "y")
                                    (aget % "source" "y"))
                                 2))
                           (+ (aget % "target" "y")
                              (/ (- (aget % "target" "y")
                                    (aget % "source" "y"))
                                 2)))))

            (->
             link-lines
             (.attr "x1" #(aget % "source" "x"))
             (.attr "x2" #(aget % "target" "x"))
             (.attr "y1" #(aget % "source" "y"))
             (.attr "y2" #(aget % "target" "y")))))
     (.start force)
     force))
