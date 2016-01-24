(ns dds.utils
  (:require [cljs-uuid-utils.core :as uuid]))

(defn deep-merge-with
  "Like merge-with, but merges maps recursively, appling the given fn
  only when there's a non-map at a particular level.

  (deepmerge + {:a {:b {:c 1 :d {:x 1 :y 2}} :e 3} :f 4}
  {:a {:b {:c 2 :d {:z 9} :z 3} :e 100}})
  -> {:a {:b {:z 3, :c 3, :d {:z 9, :x 1, :y 2}}, :e 103}, :f 4}

  taken from http://dev.clojure.org/jira/browse/CLJ-1468
  "
  [f & maps]
  (apply
   (fn m [& maps]
     (if (every? map? maps)
       (apply merge-with m maps)
       (apply f maps)))
   maps))

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
  (let [default-width 0
        parent-width (get-parent-rect-value node "width")]
    (if-not (or (nil? parent-width)
                (= parent-width 0))
      parent-width
      default-width)))

(defn get-height [node]
  (let [default-height 260
        parent-height (get-parent-rect-value node "height")]
    (if-not (or (nil? parent-height)
                (= parent-height 0))
      parent-height
      default-height)))

(defn create-element [type]
  (let [id (uuid/make-random-uuid)
        el  (.createElement js/document type)]
    (.setAttribute el "id" (uuid/uuid-string id))
    el))

(defn create-div []
  (create-element "div"))

(defn create-table []
  (create-element "table"))


(defn create-mutation-observer [f]
  (let [MutationObserver (or
                          js/window.MutationObserver
                          js/window.WebKitMutationObserver
                          js/window.MozMutationObserver)]
    (MutationObserver.
     (fn [mutations]
       (doseq [mutation mutations]
         (let [added-nodes (.-addedNodes mutation)
               removed-nodes (.-removedNodes mutation)]
           (when (or (pos? (.-length added-nodes))
                     (pos? (.-length removed-nodes)))
             (this-as this
                  (.disconnect this))
             (js/setTimeout f 10))))))))

(defn observe-inserted! [container f]
  (let [observer (create-mutation-observer f)]
    (.observe observer js/document #js {"attributes" false
                                        "childList" true
                                        "characterData" false
                                        "subtree" true})))

(defn on-window-resize! [f]
  (set! (.-onresize js/window) f))
