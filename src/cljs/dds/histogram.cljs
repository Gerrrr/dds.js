(ns dds.histogram
  (:require
   [cljs.pprint :refer [pprint]]
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.protocols :as ps]
   [dds.c3 :as c3]))

(defn get-parent-rect-value [node key]
  "Copied from https://github.com/masayuki0812/c3/blob/f56e853237ca84c372fd5dcb795491a8ebc6468d/c3.js#L2699"
  (loop [node node]
    (if (and node
             (not= (.-tagName node) "BODY"))
      (let [v
            (try
              (-> (.getBoundingClientRect node)
                  (aget key))
              (catch js/Error e
                  (when (= key "width")
                    "In IE in certain cases getBoundingClientRect
                     will cause unspecified error"
                  (.-offsetWidth node))))]
        (if v
          v
          (recur (.-parentNode node)))))))

(defn get-width [node]
  (let [default-width 640
        parent-width (get-parent-rect-value node "width")]
    (if parent-width
      parent-width
      default-width)))

(defn get-height [node]
  (let [default-height 480
        parent-height (get-parent-rect-value node "height")]
    (if parent-height
      default-height)))

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
  (let [width (get-width container)
         height (get-height container)
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
   (let [container (.createElement js/document "div")
         bin-maps (->>
                   (partition 2 1 bins)
                   (mapv
                    (fn [freq [start end]]
                      {:y freq
                       :start start
                       :end end})
                    frequencies))
         render-fn #(render-histogram container title bin-maps)
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
