(defproject ring-cors "0.1.14-SNAPSHOT"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :deploy-repositories [["releases" :clojars]]
  :plugins [[jonase/eastwood "0.3.4"]
            [lein-cljsbuild "1.1.7"]
            [lein-difftest "2.0.0"]
            [lein-doo "0.1.11"]]
  :eastwood {:exclude-linters [:bad-arglists]}
  :aliases {"ci" ["do"
                  ["clean"]
                  ["difftest"]
                  ["doo" "node" "node" "once"]
                  ["doo" "phantom" "none" "once"]
                  ["doo" "phantom" "advanced" "once"]
                  ["lint"]]
            "lint" ["do" ["eastwood"]]}
  :profiles {:provided {:dependencies [[org.clojure/clojurescript "1.10.439"]]}}
  :cljsbuild
  {:builds
   [{:id "none"
     :compiler
     {:main ring.middleware.cors.runner
      :optimizations :none
      :output-dir "target/none"
      :output-to "target/none.js"
      :parallel-build true
      :pretty-print true
      :verbose false}
     :source-paths ["src" "test"]}
    {:id "node"
     :compiler
     {:main ring.middleware.cors.runner
      :optimizations :none
      :output-dir "target/node"
      :output-to "target/node.js"
      :parallel-build true
      :pretty-print true
      :target :nodejs
      :verbose false}
     :source-paths ["src" "test"]}
    {:id "advanced"
     :compiler
     {:main ring.middleware.cors.runner
      :optimizations :advanced
      :output-dir "target/advanced"
      :output-to "target/advanced.js"
      :parallel-build true
      :pretty-print true
      :verbose false}
     :source-paths ["src" "test"]}]})
