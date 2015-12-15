(ns dds.heatmap
  (:require
   [schema.core :as s :include-macros true]
   [dds.protocols :as ps]))


(s/defrecord Heatmap
    [values :- [[s/Num]]
     row-names :- [s/Str]
     col-names :- [s/Str]]
  ps/Renderable
  (render [_]
          (let [width 300
                height 400
                margin 0
                x (->
                   (.-scale js/d3)
                   (.ordinal)
                   (.domain col-names)
                   (.rangeBands [0 width]))
                y (->
                   (.-scale js/d3)
                   (.ordinal)
                   (.domain row-names)
                   (.rangeBands [height 0]))
                flat-vals (flatten values)
                z-domain [(apply min flat-vals) (apply max flat-vals)]
                container (.createElement js/document "div")
                z (->
                   (.scale js/chroma ["white" "red"])
                   (.domain z-domain))
                ]
            {:x x
             :y y
             :flat-vals flat-vals
             :z-domain z-domain
             :z z}
            )))
