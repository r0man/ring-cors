(defproject ring-cors "0.0.2-SNAPSHOT"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :dev-dependencies [[org.clojars.rayne/autodoc "0.8.0-SNAPSHOT"]
                     [swank-clojure "1.2.1"]]
  :autodoc
  {:name "Cross-Origin Resource Sharing"
   :description "Ring middleware for Cross-Origin Resource Sharing."
   :copyright "Copyright 2011 Roman Scherer"
   :root "."
   :source-path ""
   :web-src-dir "http://github.com/r0man/ring-cors/blob/"
   :web-home "http://github.com/r0man/ring-cors"
   :output-path "autodoc"
   :namespaces-to-document ["ring"]
   :load-except-list [#"/test/" #"project\.clj"]})
