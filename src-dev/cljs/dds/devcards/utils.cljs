(ns dds.devcards.utils)

 (defn- set-content! [node html-obj]
     (set! (.-innerHTML node) "")
     (.appendChild node html-obj))
