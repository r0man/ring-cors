(ns ring.middleware.cors-test
  (:require [clojure.test :refer :all]
            [ring.middleware.cors :refer :all]))

(deftest test-allow-request?
  (testing "with empty vector"
    (is (not (allow-request? {:headers {"origin" "http://eample.com"}}
                             {:access-control-allow-origin []}))))
  (testing "with one regular expressions"
    (are [origin expected]
      (is (= expected
             (allow-request?
              {:headers {"origin" origin}
               :request-method :get}
              {:access-control-allow-origin [#"http://(.*\.)?burningswell.com"]
               :access-control-allow-methods #{:get :put :post}})))
      nil false
      "" false
      "http://example.com" false
      "http://burningswell.com" true))
  (testing "with multiple regular expressions"
    (are [origin expected]
      (is (= expected
             (allow-request?
              {:headers {"origin" origin}
               :request-method :get}
              {:access-control-allow-origin
               [#"http://(.*\.)?burningswell.com"
                #"http://example.com"]
               :access-control-allow-methods #{:get :put :post}})))
      nil false
      "" false
      "http://example.com" true
      "http://burningswell.com" true
      "http://api.burningswell.com" true
      "http://dev.burningswell.com" true)))

(defn handler [request]
  ((wrap-cors (fn [_] {})
              :access-control-allow-origin #"http://example.com"
              :access-control-allow-headers #{:x-custom-header}
              :access-control-allow-methods #{:get :put :post})
   request))

(deftest test-preflight
  (testing "whitelist concrete headers"
    (let [headers {"origin" "http://example.com"
                   "access-control-request-method" "POST"
                   "access-control-request-headers" "x-custom-header"}]
    (is (= {:status 200,
            :headers {"Access-Control-Allow-Origin" "http://example.com"
                      "Access-Control-Allow-Headers" "X-Custom-Header"
                      "Access-Control-Allow-Methods" "GET, POST, PUT"}
            :body "preflight complete"}
           (handler {:request-method :options
                     :uri "/"
                     :headers headers})))))

  (testing "whitelist any headers"
    (is (= {:status 200,
            :headers {"Access-Control-Allow-Origin" "http://example.com"
                      "Access-Control-Allow-Headers" "X-Bar, X-Foo"
                      "Access-Control-Allow-Methods" "GET, POST, PUT"}
            :body "preflight complete"}
           ((wrap-cors (fn [_] {})
                       :access-control-allow-origin #"http://example.com"
                       :access-control-allow-headers :any
                       :access-control-allow-methods #{:get :put :post})
            {:request-method :options
             :uri "/"
             :headers {"origin" "http://example.com"
                       "access-control-request-method" "POST"
                       "access-control-request-headers" "x-foo, x-bar"}}))))

  (testing "method not allowed"
    (is (nil? (handler
               {:request-method :options
                :uri "/"
                :headers {"origin" "http://example.com"
                          "access-control-request-method" "DELETE"}}))))

  (testing "header not allowed"
    (let [headers {"origin" "http://example.com"
                   "access-control-request-method" "GET"
                   "access-control-request-headers" "x-another-custom-header"}]
    (is (nil? (handler
               {:request-method :options
                :uri "/"
                :headers headers}))))))

(deftest test-cors
  (testing "success"
    (is (= {:headers {"Access-Control-Allow-Methods" "GET, POST, PUT",
                      "Access-Control-Allow-Origin" "http://example.com"}}
           (handler {:request-method :post
                     :uri "/"
                     :headers {"origin" "http://example.com"}}))))
  (testing "failure"
    (is (nil? (handler {:request-method :get
                        :uri "/"
                        :headers {"origin" "http://foo.com"}})))))

(deftest test-no-cors-header-when-handler-returns-nil
  (is (nil? ((wrap-cors (fn [_] nil)
                        :access-control-allow-origin #".*example.com")
             {:request-method
              :get :uri "/"
              :headers {"origin" "http://example.com"}}))))

(deftest test-options-without-cors-header
  (is (nil? ((wrap-cors
              (fn [_] nil)
              :access-control-allow-origin #".*example.com")
             {:request-method :options :uri "/"}))))

(deftest test-method-not-allowed
  (is (nil? ((wrap-cors
              (fn [_] nil)
              :access-control-allow-origin #".*"
              :access-control-allow-methods [:get :post :patch :put :delete])
             {:request-method :options
              :headers {"origin" "http://foo.com"}
              :uri "/"}))))
