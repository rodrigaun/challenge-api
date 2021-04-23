(ns challenge-api.components.http-server.router
  (:require [challenge-api.components.http-server.validations :as validations]
            [ring.util.http-status :as http-status]
            [medley.core :as medley])
  (:import clojure.lang.ExceptionInfo))

(defn format-response
  ([status-code response request-body]
   {:status status-code
    :body   response})
  ([status-code response request-body exception]
   {:status status-code
    :body   response}))

(defn message [messages message-key]
  {:message (get messages message-key)})

(defn exception-info-handling [exception error-message-type body-params messages]
  (let [message-error (message messages error-message-type)
        {:keys [type status]
         :or   {status http-status/bad-request}
         :as   data-exception} (ex-data exception)
        response (dissoc data-exception :type :status)]
    (condp = type
      :validation (format-response status (merge message-error response) body-params exception)
      :business-rules (format-response status (merge {:message (.getMessage exception)} response) body-params exception)
      (format-response http-status/internal-server-error message-error body-params exception))))

(defn route-manager 
  [{:keys [fields-validation request-definition fields-parse messages]}
   request-type
   {:keys [body-params path-params query-params] :as request}]
  (let [{:keys [error-message-type handler required-fields]} (request-type request-definition)]
    (try
      (let [body (medley/map-keys keyword body-params)
            params (medley/map-keys keyword path-params)
            query (medley/map-keys keyword query-params)]
        (validations/request-validation fields-validation required-fields body :body-params messages)
        (validations/request-validation fields-validation required-fields params :path-params messages)
        (let [parsing (partial validations/fields-parser fields-parse)
              {:keys [status response]} (handler {:body         (parsing body)
                                                  :path-params  (parsing params)
                                                  :query-params (parsing query)
                                                  :request      request})]
          (format-response status response body-params)))
      (catch NoSuchFieldException e
        (format-response http-status/bad-request {:message (.getMessage e)} body-params e))
      (catch ExceptionInfo e
        (exception-info-handling e error-message-type body-params messages))
      (catch Exception e
        (format-response http-status/internal-server-error (message messages error-message-type) body-params e)))))