(defproject ring-cors "0.1.8-SNAPSHOT"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.2"]]
  :deploy-repositories [["releases" :clojars]]
  :plugins [[jonase/eastwood "0.2.1"]
            [lein-bikeshed "0.1.6"]
            [lein-difftest "2.0.0"]]
  :eastwood {:exclude-linters [:bad-arglists]}
  :aliases {"ci" ["do" ["with-profile" "dev:dev,1.4:dev,1.5:dev" "difftest"] ["lint"]]
            "lint" ["do" ["bikeshed"] ["eastwood"]]}
  :profiles
  {:1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
   :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}})
