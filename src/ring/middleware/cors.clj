(ns ring.middleware.cors
  "Ring middleware for Cross-Origin Resource Sharing."
  (:require [clojure.string :refer [capitalize join split]]))

(defn origin
  "Returns the Origin request header."
  [request] (get (:headers request) "origin"))

(defn allow-request?
  "Returns true if the request's origin matches the access control
  origin, otherwise false."
  [request access-control]
  (let [origin (origin request)
        allowed (:access-control-allow-origin access-control)]
    (if (and origin allowed (some #(re-matches % origin) (if (sequential? allowed) allowed [allowed])))
      true false)))

(defn header-name
  "Returns the capitalized header name as a string."
  [header] (if header (join "-" (map capitalize (split (name header) #"-")))))

(defn normalize-headers
  "Normalize the headers by converting them to capitalized strings."
  [headers] (reduce #(assoc %1 (header-name (first %2)) (last %2)) {} headers))

(defn add-access-control
  "Add the access control headers using the request's origin to the response."
  [request response access-control]
  (if-let [origin (origin request)]
    (let [access-headers (normalize-headers (assoc access-control :access-control-allow-origin origin))]
      (assoc response :headers (merge (:headers response) access-headers)))
    response))

(defn wrap-cors
  "Middleware that adds Cross-Origin Resource Sharing headers.

Example:

  (def handler
    (-> routes
        (wrap-cors
         :access-control-allow-origin #\"http://example.com\")))
"
  [handler & access-control]
  (let [access-control (apply hash-map access-control)]
    (fn [request]
      (let [response (handler request)]
        (if (and response (allow-request? request access-control))
          (add-access-control request response access-control)
          response)))))
