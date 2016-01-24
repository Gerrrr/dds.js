(ns dds.devcards
  (:require
   [devcards.core :as dc :include-macros true]
   [dds.devcards.pie]
   [dds.devcards.bar]
   [dds.devcards.scatter]
   [dds.devcards.histogram]
   [dds.devcards.heatmap]
   [dds.devcards.kvs]
   [dds.devcards.graph]
   [dds.devcards.table])
  (:require-macros
   [devcards.core :refer [defcard defcard-doc]]))

(enable-console-print!)
(devcards.core/start-devcard-ui!)
