# Ring CORS [![Build Status](https://travis-ci.org/r0man/ring-cors.png)](https://travis-ci.org/r0man/ring-cors)

Ring middleware for Cross-Origin Resource Sharing.

## Installation

Via Clojars: https://clojars.org/ring-cors

## Usage

    (use 'ring.middleware.cors)

    (def handler
      (-> my-routes
          (wrap-cors
           :access-control-allow-origin #"http://example.com")))

## License

Copyright (C) 2013 Roman Scherer

Distributed under the Eclipse Public License, the same as Clojure.
