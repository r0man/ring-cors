(defproject ring-cors "0.0.2-SNAPSHOT"
  :description "Ring middleware for Cross-Origin Resource Sharing."
  :url "http://github.com/r0man/ring-cors"
  :autodoc {:name "Cross-Origin Resource Sharing"
            :description "Ring middleware for Cross-Origin Resource Sharing."
            :copyright "Copyright 2011 Roman Scherer"
            :root "."
            :source-path ""
            :web-src-dir "http://github.com/r0man/ring-cors/blob/"
            :web-home "http://github.com/r0man/ring-cors"
            :output-path "autodoc"
            :namespaces-to-document ["ring"]
            :load-except-list [#"/test/" #"project\.clj"]}
  :dependencies [[org.clojure/clojure "1.3.0-RC0"]]
  :multi-deps {"1.2.1" [[org.clojure/clojure "1.2.1"]]})
