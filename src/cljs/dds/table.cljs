(ns dds.table
  (:require
   [schema.core :as s :include-macros true]
   [plumbing.core :as p]
   [dds.utils :as du]))



(def numeric-types #{"integer" "double" "float" "byte" "short" "long" "decimal"
                     "date" "timestamp"})
(def valid-types (into #{"string" "boolean"} numeric-types))



(defn- dimension-name [type-map]
  (str
   (p/safe-get type-map :name)
   " ["
   (if (contains? numeric-types (p/safe-get type-map :type))
     "numeric"
     "string")
   (if (p/safe-get type-map :nullable)
     "*"
     "")
   "]"))

(def slickgrid-options #js {"enableCellNavigation" true
                            "enableColumnReorder" false
                            "multiColumnSort" false
                            "defaultColumnWidth" 140})

(defn update-grid [data-view data]
  (.beginUpdate data-view)
  (.setItems data-view data)
  (.endUpdate data-view))

(defn render-loop
  [pager-div grid-div schema content]
  (set! (.-innerHTML pager-div) "")
  (set! (.-innerHTML grid-div) "")
  (set! (.-height (.-style grid-div))
        (-> (du/get-height grid-div)
            (str "px")))

  (let [table-maps (->> content
                        (map-indexed
                         (fn [row-id row]
                           (->> row
                                (map-indexed
                                 (fn [col-idx cell]
                                   [(dimension-name (nth schema col-idx))
                                    cell]))
                                (into {"id" row-id}))))
                        (clj->js))
        column-keys (.keys js/d3 (first table-maps))
        columns (->>
                 column-keys
                 (map
                  (fn [k]
                    (let [column {"id" k
                                  "name" k
                                  "field" k
                                  "sortable" true}
                          column (if (= k "id")
                                   (assoc column "width" 40)
                                   column)]
                      column)))
                 (clj->js))
        data-view (js/Slick.Data.DataView.)
        grid (js/Slick.Grid. grid-div data-view columns slickgrid-options)
        pager (js/Slick.Controls.Pager. data-view grid (js/$ pager-div))
        sort-column (first column-keys)
        compare (fn [a b]
                  (let [x (aget a sort-column)
                        y (aget b sort-column)]
                    (cond
                      (= x y) 0
                      (> x y) 1
                      :else -1)))]

    (-> (.-onRowCountChanged data-view)
        (.subscribe
         (fn []
           (.updateRowCount grid)
           (.render grid))))

    (-> (.-onRowsChanged data-view)
        (.subscribe
         (fn [_ args]
           (.invalidateRows grid (.-rows args))
           (.render grid))))

    (-> (.-onSort grid)
        (.subscribe
         (fn [e args]
           (if (and
                (.-msie (.-browser js/$))
                (<= (.-version (.-browser js/$)) 8))
             (.fastSort data-view sort-column (.-sortAsc args))
             (.sort data-view compare (.-sortAsc args))))))

    (update-grid data-view table-maps)))

(s/defn ^:always-validate
  render :- js/Element
  [title :- s/Str
   schema :- [{(s/required-key :name) s/Str
               (s/required-key :nullable) s/Bool
               (s/required-key :type) (apply s/enum valid-types)}]
   content :- [[s/Any]]]
  (let [container (du/create-div)
        grid-div (du/create-div)
        pager-div (du/create-div)
        title-div (du/create-title-div title)
        render-fn #(render-loop pager-div grid-div schema content)]
    (.add (.-classList grid-div) "grid")
    (.add (.-classList pager-div) "pager")
    (.appendChild container title-div)
    (.appendChild container pager-div)
    (.appendChild container grid-div)
    (du/observe-inserted! container render-fn)
    (du/on-window-resize! render-fn)
    container))
