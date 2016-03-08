(ns dds.utils
  (:require [cljs-uuid-utils.core :as uuid]))

(defn nodelist->vector [nodelst]
  (let [length (.-length nodelst)
        acc (array)]
    (loop [i 0]
      (if (< i length)
        (do
          (.push acc (aget nodelst i))
          (recur  (inc i)))
        (into [] acc)))))

(defn get-parent-rect-value [el key]
  "Copied from https://github.com/masayuki0812/c3/blob/f56e853237ca84c372fd5dcb795491a8ebc6468d/c3.js#L2699"
  (loop [node el]
    (let [v
          (try
            (-> (.getBoundingClientRect node)
                (aget key))
            (catch js/Error e
              (when (= key "width")
                "In IE in certain cases getBoundingClientRect
                     will cause unspecified error"
                (.-offsetWidth node))))]
      (if (and (not (nil? v)) (> v 0))
        v
        (recur (.-parentNode node))))))

(defn get-siblings [obj]
  (let [obj-html (.-innerHTML obj)]
    (->>
     (aget obj "parentNode" "childNodes")
     (nodelist->vector)
     (filter #(not (= obj-html (.-innerHTML %)))))))

(defn get-width [node]
  (get-parent-rect-value node "width"))

(defn get-height [node]
  (let [container-height (max (get-parent-rect-value node "height") 320)
        sibling-heights (->> (get-siblings node)
                             (map #(-> (.getBoundingClientRect %)
                                       (aget "height"))))]
    (if (empty? sibling-heights)
      container-height
      (apply - container-height sibling-heights))))

(defn create-element [type]
  (let [id (uuid/make-random-uuid)
        el  (.createElement js/document type)]
    (.setAttribute el "id" (uuid/uuid-string id))
    el))

(defn create-div []
  (create-element "div"))

(defn create-table []
  (create-element "table"))

(defn create-title-div [text]
  (let [el (create-div)]
    (.add (.-classList el) "dds-title")
    (set! (.-innerHTML el) (str "<strong>" text "</strong>"))
    el))

(defn wrap-with-title [div title]
  (let [container (create-div)
        title-div (create-title-div title)]
    (.add (.-classList container) "dds-content")
    (.appendChild container title-div)
    (.appendChild container div)
    container))

(defn create-mutation-observer [f obj]
  (let [MutationObserver (or
                          js/window.MutationObserver
                          js/window.WebKitMutationObserver
                          js/window.MozMutationObserver)]
    (MutationObserver.
     (fn [mutations]
       (doseq [mutation mutations]
         (when (and (= (.-type mutation) "childList")
                    (> (.-length (.-addedNodes mutation)) 0))
           (let [added-nodes (nodelist->vector (.-addedNodes mutation))]
             (when (some #(= obj %) added-nodes)
               (this-as this (.disconnect this))
               (js/setTimeout f 10)))))))))

(defn observe-inserted! [container f]
  (let [observer (create-mutation-observer f container)]
    (.observe observer js/document #js {"childList" true
                                        "subtree" true})))

(defn on-window-resize! [f]
  (set! (.-onresize js/window) f))
