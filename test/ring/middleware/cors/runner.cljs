(ns ring.middleware.cors.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [ring.middleware.cors-test]))

(doo-tests 'ring.middleware.cors-test)
