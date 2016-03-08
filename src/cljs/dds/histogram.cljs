(ns dds.histogram
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.utils :as du])
  (:import
   [goog.string format]))

(def margins
  {:left-margin 50
   :bottom-margin 60
   :right-margin 10
   :top-margin 10})

(def tooltip-template
"<div class=\"c3-tooltip-container\">
  <table class=\"c3-tooltip\">
    <tbody>
      <tr>
        <th colspan=\"2\">%s - %s</th>
      </tr>
      <tr class=\"c3-tooltip-name-x\">
        <td class=\"name\">Area</td>
        <td class=\"value\">%.2f</td>
     </tr>
    </tbody>
  </table>
</div>")

(s/defn ^:always-validate render-loop
  [container :- js/Element
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
                          :scaledStart scaled-start
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
                        :scaledHeight (y-domain height))))
              (clj->js))
        tip (->
             (.tip js/d3)
             (.attr "class" "d3-tip")
             (.offset #js [-10 0])
             (.html (fn [bin]
                      (format tooltip-template
                              (.-start bin)
                              (.-end bin)
                              (* (- (.-end bin) (.-start bin))
                                 (.-height bin))))))]
    (.call svg tip)
     (->
      (.selectAll svg ".bin")
      (.data bins)
      (.enter)
      (.append "rect")
      (.on "mouseover" (.-show tip))
      (.on "mouseout" (.-hide tip))
      (.attr "class" "bin")
      (.attr "x" #(aget % "scaledStart"))
      (.attr "width" #(dec (aget % "width")))
      (.attr "y" #(aget % "scaledHeight"))
      (.attr "height" #(- height (aget % "scaledHeight"))))
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

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   bins :- [s/Num]
   frequencies :- [s/Num]]
  {:pre [(or (and (even? (count bins))
                   (odd? (count frequencies)))
              (and (odd? (count bins))
                   (even? (count frequencies))))]}
  (let [chart (du/create-div)
        bin-maps (->>
                  (partition 2 1 bins)
                  (mapv
                   (fn [freq [start end]]
                     {:y freq
                      :start start
                      :end end})
                   frequencies))
        render-fn #(render-loop chart bin-maps)
        container (du/wrap-with-title chart title)]
    (du/observe-inserted! container render-fn)
    container))
