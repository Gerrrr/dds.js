(ns dds.protocols)

(defprotocol Renderable
  (render [chart]
    "Renders chart"))
