(ns ring.middleware.cors-test
  (:require [ring.middleware.cors :refer :all]
            [clojure.test :refer :all]))

(deftest test-allow-request?
  (testing "with empty vector"
    (is (not (allow-request? {:headers {"origin" "http://eample.com"}} {:access-control-allow-origin []}))))
  (testing "with one regular expressions"
    (are [origin expected]
      (is (= expected (allow-request? {:headers {"origin" origin}} {:access-control-allow-origin #"http://(.*\.)?burningswell.com"})))
      nil false
      "" false
      "http://example.com" false
      "http://burningswell.com" true))
  (testing "with multiple regular expressions"
    (are [origin expected]
      (is (= expected (allow-request? {:headers {"origin" origin}} {:access-control-allow-origin [#"http://(.*\.)?burningswell.com" #"http://example.com"]})))
      nil false
      "" false
      "http://example.com" true
      "http://burningswell.com" true
      "http://api.burningswell.com" true
      "http://dev.burningswell.com" true)))

(deftest test-header-name
  (are [header expected]
    (is (= expected (header-name header)))
    nil nil
    :access-control-allow-origin "Access-Control-Allow-Origin"
    "Access-Control-Allow-Origin" "Access-Control-Allow-Origin"))

(deftest test-normalize-headers
  (are [headers expected]
    (is (= expected (normalize-headers headers)))
    nil {}
    {:access-control-allow-origin "*"} {"Access-Control-Allow-Origin" "*"}
    {"Access-Control-Allow-Origin" "*"} {"Access-Control-Allow-Origin" "*"}))

(deftest test-add-access-control
  (let [origin "http://example.com"]
    (is (= {} (add-access-control {} {} {})))
    (is (= {:headers {"Access-Control-Allow-Origin" "http://example.com"}}
           (add-access-control {:headers {"origin" origin}} {} {:access-control-allow-origin #".*\.example.com"})))))

(deftest test-wrap-cors
  (let [response
        ((wrap-cors
          (fn [_] {})
          :access-control-allow-origin #".*example.com"
          :access-control-expose-headers ["X-Pagination-Page" "X-Pagination-Per-Page" "X-Pagination-Total"]
          :access-control-request-headers "X-PINGOTHER"
          :access-control-request-method ["POST" "PUT"])
         {:request-method :get :uri "/" :headers {"origin" "http://example.com"}})]
    (let [headers (:headers response)]
      (is (= "http://example.com" (get headers "Access-Control-Allow-Origin")))
      (is (= ["POST" "PUT"] (get headers "Access-Control-Request-Method")))
      (is (= "X-PINGOTHER" (get headers "Access-Control-Request-Headers")))
      (is (= ["X-Pagination-Page" "X-Pagination-Per-Page" "X-Pagination-Total"]
             (get headers "Access-Control-Expose-Headers"))))))
