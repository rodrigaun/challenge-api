(ns challenge-api.modules.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [clojure.data.codec.base64 :as b64]
            [ring.util.http-status :as http-status]
            [challenge-api.responses :as responses]))

(defn extract-basic [headers]
  (let [result (-> headers
                   (get "authorization")
                   (str/split #" ")
                   second
                   .getBytes
                   b64/decode
                   (String. "UTF-8")
                   (str/split #":"))]
    {:email (first result)
     :pass  (second result)}))

(defn extract-bearer [headers]
  (-> headers
      (get "authorization")
      (str/split #" ")
      second))

(defn get-token [client secret]
  (jwt/sign client secret))

(defn validate-token [headers persistence]
  (try
    (let [token-jwt (extract-bearer headers)
        secret (get-in persistence [:client-db :secret-jwt])]
    (jwt/unsign token-jwt secret))

    (catch Exception e
      (throw (ex-info (:login-token-error responses/messages) {:type   :business-rules
                                                               :status http-status/forbidden})))))