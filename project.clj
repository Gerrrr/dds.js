(defproject dds.js "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :plugins [[lein-ancient "0.6.8"]
            [lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [prismatic/schema "1.0.3"]
                 [prismatic/plumbing "0.5.2"]
                 [devcards "0.2.1"]]
  :figwheel {:css-dirs ["resources/public/css"]}
  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src/cljs/" "src-dev/cljs"]
     :figwheel {:devcards true }
     :compiler {:output-to "resources/public/js/dev/compiled/dds.js"
                :output-dir "resources/public/js/dev/compiled/out"
                :asset-path "js/dev/compiled/out"
                :main "dds.devcards"
                :optimizations :none
                :source-map true}}
    {:id "release"
     :source-paths ["src/cljs/"]
     :compiler {:output-to "resources/public/js/release/dds.intermediate.js"
                :output-dir "resources/public/js/release/out"
                :pretty-print false
                :whitespace false
                :optimizations :advanced
                :externs ["resources/externs/c3.js"]}
     :notify-command ["scripts/build.sh"]}]})
