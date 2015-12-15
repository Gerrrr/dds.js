(ns dds.c3)

(defn generate-element [m]
  (->>
   (clj->js m)
   (.generate js/c3)
   (.-element)))
