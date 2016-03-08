(ns dds.graph
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.utils :as du]))

(def margins
  {:left-margin 0
   :bottom-margin 0
   :right-margin 0
   :top-margin 0})


(defn style-edge-lines [edge-lines show-directions?]
  (.attr edge-lines "marker-end" (if show-directions?
                                   "url(#triangle)"
                                   "")))

(defn style-edge-labels [edge-labels show-edge-labels?]
  (->
   (.attr edge-labels "fill" "black")
   (.attr "text-anchor" "middle")
   (.style "visibility" (if show-edge-labels?
                          "visible"
                          "hidden"))))

(defn style-node-labels [node-labels show-node-labels?]
  (->
   (.attr node-labels "fill" "black")
   (.style "visibility" (if show-node-labels?
                          "visible"
                          "hidden"))))

(s/defn ^:always-validate render-loop
  [container :- js/Element
   force :- js/Object
   nodes :- [{(s/required-key :label) s/Str}]
   links :- [{(s/required-key :source) s/Num
               (s/required-key :target) s/Num
               (s/required-key :label) s/Str}]
   show-node-labels? :- s/Bool
   show-edge-labels? :- s/Bool
   show-directions? :- s/Bool]
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
         edge-lines (->
                     (.append links "line")
                     (.attr "class" "link")
                     (style-edge-lines show-directions?))
         edge-labels (->
                      (.append links "text")
                      (.text #(.-label %))
                      (.attr "class" "edgeLabel")
                      (style-edge-labels show-edge-labels?))
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
                      (.attr "class" "nodeLabel")
                      (style-node-labels show-node-labels?))]
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
             edge-labels
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
             edge-lines
             (.attr "x1" #(aget % "source" "x"))
             (.attr "x2" #(aget % "target" "x"))
             (.attr "y1" #(aget % "source" "y"))
             (.attr "y2" #(aget % "target" "y")))))
     (.start force)
     force))

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   vertices :- [s/Str]
   edges :- [[(s/one s/Num "source")
              (s/one s/Num "target")
              (s/one s/Str "label")]]
   show-node-labels? :- s/Bool
   show-edge-labels? :- s/Bool
   show-directions? :- s/Bool]
  (let [nodes (map (fn [v] {:label v}) vertices)
        links (map
               (fn [[s t l]] {:source s :target t :label l})
               edges)
        force (->
               (.-layout js/d3)
               (.force))
        chart (du/create-div)
        render-fn #(render-loop chart force nodes links
                                show-node-labels? show-edge-labels?
                                show-directions?)
        container (du/wrap-with-title chart title)]
    (du/observe-inserted! container render-fn)
    container))
