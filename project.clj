(defproject ring-cors "0.1.12"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :deploy-repositories [["releases" :clojars]]
  :plugins [[jonase/eastwood "0.2.5"]
            [lein-bikeshed "0.5.1"]
            [lein-difftest "2.0.0"]]
  :eastwood {:exclude-linters [:bad-arglists]}
  :aliases {"ci" ["do" ["difftest"] ["lint"]]
            "lint" ["do" ["bikeshed"] ["eastwood"]]})
