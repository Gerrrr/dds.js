(ns dds.histogram
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.c3 :as c3]
   [dds.protocols :as ps]
   [dds.utils :as du]))

(defn get-margins [node]
  {:left-margin 50
   :bottom-margin 20
   :right-margin 10
   :top-margin 10})

(s/defn ^:always-validate render-histogram
  [container
   title
   bin-maps :- [{(s/required-key :start) s/Num
                (s/required-key :end) s/Num
                (s/required-key :y) s/Num}]]
  (set! (.-innerHTML container) "")
  (let [width (du/get-width container)
        height (du/get-height container)
        {:keys [left-margin right-margin
                top-margin bottom-margin]
         :as m} (get-margins container)
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
                  (.range (clj->js [0 width]))
                  (.domain (clj->js [(apply min (map :start bin-maps))
                                     (apply max (map :end bin-maps))])))
        bins (map
               (fn [{:keys [start end y]
                     :as m}]
                 (assoc m
                        :width (- (x-domain end) (x-domain start))
                        :height (/ y (- end start))))
               bin-maps)
        y-domain (->
                  (.-scale js/d3)
                  (.linear)
                  (.range (clj->js [height 0]))
                  (.domain (clj->js [0 (apply max (map :height bins))])))
        bins (map
              (fn [bin]
                (assoc bin
                       :height (y-domain (p/safe-get bin :height))
                       :start (x-domain (p/safe-get bin :start)))) bins)]
     (->
      (.selectAll svg ".bin")
      (.data (clj->js bins))
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




(s/defrecord Histogram
  [title :- s/Str
   bins :- [s/Num]
   frequencies :- [s/Num]]
  ps/Renderable
  (render
   [_]
   (let [container (du/create-div)
         bin-maps (->>
                   (partition 2 1 bins)
                   (mapv
                    (fn [freq [start end]]
                      {:y freq
                       :start start
                       :end end})
                    frequencies))
         render-fn #(render-histogram container title bin-maps)
         observer (du/create-mutation-observer render-fn)]
     (set! (.-onresize js/window) render-fn)
     (.observe observer js/document #js {"attributes" true
                                         "childList" true
                                         "characterData" true
                                         "subtree" true})
     container)))
