(defproject ring-cors "0.1.9-SNAPSHOT"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]]
  :deploy-repositories [["releases" :clojars]]
  :plugins [[jonase/eastwood "0.2.3"]
            [lein-bikeshed "0.3.0"]
            [lein-difftest "2.0.0"]]
  :eastwood {:exclude-linters [:bad-arglists]}
  :aliases {"ci" ["do" ["difftest"] ["lint"]]
            "lint" ["do" ["bikeshed"] ["eastwood"]]})
