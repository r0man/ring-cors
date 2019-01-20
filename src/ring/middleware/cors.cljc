(ns ring.middleware.cors
  "Ring middleware for Cross-Origin Resource Sharing."
  (:require [clojure.set :as set]
            [clojure.string :as str]))

;; find-header / get-header have been copied from ring-core
;; to reduce dependency on external libs

(def preflight-complete-response
  "The default preflight complete response."
  {:status 200
   :headers {}
   :body "preflight complete"})

(defn- find-header
  "Looks up a header in a Ring response (or request) case insensitively,
  returning the header map entry, or nil if not present."
  [resp ^String header-name]
  (let [header-name (str/upper-case header-name)]
    (->> (:headers resp)
         (filter #(= header-name (str/upper-case (key %))))
         (first))))

(defn- get-header
  "Looks up a header in a Ring response (or request) case insensitively,
  returning the value of the header, or nil if not present."
  [resp header-name]
  (some-> resp (find-header header-name) val))

(defn origin
  "Returns the Origin request header."
  [request] (get-header request "origin"))

(defn preflight?
  "Returns true if the request is a preflight request"
  [request]
  (= (request :request-method) :options))

(defn lower-case-set
  "Converts strings in a sequence to lower-case, and put them into a set"
  [s]
  (->> s
       (map str/trim)
       (map str/lower-case)
       (set)))

(defn parse-headers
  "Transforms a comma-separated string to a set"
  [s]
  (->> (str/split (str s) #",")
       (remove str/blank?)
       (lower-case-set)))

(defn allow-preflight-headers?
  "Returns true if the request is a preflight request and all the headers that
  it's going to use are allowed. Returns false otherwise."
  [request allowed-headers]
  (if (nil? allowed-headers)
    true
    (set/subset?
     (parse-headers (get-header request "access-control-request-headers"))
     (lower-case-set (map name allowed-headers)))))

(defn allow-method?
  "In the case of regular requests it checks if the request-method is allowed.
  In the case of preflight requests it checks if the
  access-control-request-method is allowed."
  [request allowed-methods]
  (let [preflight-name [:headers "access-control-request-method"]
        request-method (if (preflight? request)
                         (keyword (str/lower-case
                                   (get-in request preflight-name "")))
                         (:request-method request))]
    (contains? allowed-methods request-method)))

(defn allow-request?
  "Returns true if the request's origin matches the access control
  origin, otherwise false."
  [request access-control]
  (let [origin (origin request)
        allowed-origins (:access-control-allow-origin access-control)
        allowed-headers (:access-control-allow-headers access-control)
        allowed-methods (:access-control-allow-methods access-control)]
    (if (and origin
             (if (fn? allowed-origins)
               true
               (seq allowed-origins))
             (seq allowed-methods)
             (if (fn? allowed-origins)
               (allowed-origins request)
               (some #(re-matches % origin) allowed-origins))
             ;;...or else it's a predefined collection,
             ;;do a normal existence check.
             (if (preflight? request)
               (allow-preflight-headers? request allowed-headers)
               true)
             (allow-method? request allowed-methods))
      true false)))

(defn header-name
  "Returns the capitalized header name as a string."
  [header]
  (if header
    (->> (str/split (name header) #"-")
         (map str/capitalize )
         (str/join "-" ))))

(defn normalize-headers
  "Normalize the headers by converting them to capitalized strings."
  [headers]
  (let [upcase #(str/join ", " (sort (map (comp str/upper-case name) %)))
        to-header-names #(str/join ", " (sort (map (comp header-name name) %)))]
    (reduce
     (fn [acc [k v]]
       (assoc acc (header-name k)
              (case k
                :access-control-allow-methods (upcase v)
                :access-control-allow-headers (to-header-names v)
                v)))
     {} headers)))

(defn add-headers
  "Add the access control headers using the request's origin to the response."
  [request access-control response]
  (if-let [origin (origin request)]
    (update-in response [:headers] merge
               (assoc access-control :access-control-allow-origin origin))
    response))

(defn add-allowed-headers
  "Adds the allowed headers to the request"
  [request allowed-headers response]
  (if (preflight? request)
    (let [request-headers (get-header request "access-control-request-headers")
          allowed-headers (if (nil? allowed-headers)
                            (parse-headers request-headers)
                            allowed-headers)]
      (if allowed-headers
        (update-in response [:headers] merge
                   {:access-control-allow-headers allowed-headers})
        response))
    response))


(defn add-access-control
  "Add the access-control headers to the response based on the rules
  and what came on the header."
  [request access-control response]
  (let [allowed-headers (access-control :access-control-allow-headers)
        rest-of-headers (dissoc access-control
                                :access-control-allow-headers)
        unnormalized-resp (->> response
                               (add-headers request rest-of-headers)
                               (add-allowed-headers request allowed-headers))]
    (update-in unnormalized-resp [:headers] normalize-headers)))

(defn normalize-config
  [access-control]
  (-> (apply hash-map access-control)
      (update-in [:access-control-allow-methods] set)
      (update-in [:access-control-allow-headers] #(if (coll? %) (set %) %))
      (update-in [:access-control-allow-origin] #(if (or (sequential? %)
                                                         (fn? %))
                                                   %
                                                   [%]))))

(defn handle-cors
  "Handle a synchronous CORS `request`."
  [handler request access-control response-handler]
  (if (and (preflight? request)
           (allow-request? request access-control))
    (response-handler request access-control preflight-complete-response)
    (if (origin request)
      (if (allow-request? request access-control)
        (if-let [response (handler request)]
          (response-handler request access-control response))
        (handler request))
      (handler request))))

(defn- handle-cors-async
  "Handle an asynchronous CORS `request`."
  [handler request respond raise access-control response-handler]
  (if (and (preflight? request)
           (allow-request? request access-control))
    (respond (response-handler request access-control preflight-complete-response))
    (if (origin request)
      (if (allow-request? request access-control)
        (handler request #(respond (response-handler request access-control %)) raise)
        (handler request respond raise))
      (handler request respond raise))))

(defn wrap-cors
  "Middleware that adds Cross-Origin Resource Sharing headers.

  (def handler
    (-> routes
        (wrap-cors
         :access-control-allow-origin #\"http://example.com\"
         :access-control-allow-methods [:get :put :post :delete])))
  "
  [handler & access-control]
  (let [access-control (normalize-config access-control)]
    (fn
      ([request]
       (handle-cors handler request access-control add-access-control))
      ([request respond raise]
       (handle-cors-async handler request respond raise access-control add-access-control)))))
