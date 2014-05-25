# Ring CORS
  [![Build Status](https://travis-ci.org/r0man/ring-cors.png)](https://travis-ci.org/r0man/ring-cors)
  [![Dependencies Status](http://jarkeeper.com/r0man/ring-cors/status.png)](http://jarkeeper.com/r0man/ring-cors)

Ring middleware for Cross-Origin Resource Sharing.

## Installation

Via Clojars: https://clojars.org/ring-cors

[![Current Version](https://clojars.org/ring-cors/latest-version.svg)](https://clojars.org/ring-cors)

## Usage

    (require '[ring.middleware.cors :refer [wrap-cors]])

    (def handler
      (wrap-cors my-routes :access-control-allow-origin #"http://example.com"
                           :access-control-allow-methods [:get :put :post :delete]))

## License

Copyright (C) 2013 Roman Scherer

Distributed under the Eclipse Public License, the same as Clojure.
