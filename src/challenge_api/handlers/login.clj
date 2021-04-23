(ns challenge-api.handlers.login
  (:require [ring.util.http-status :as http-status]
            [challenge-api.components.postgres.persistence :as p]
            [challenge-api.modules.auth :as auth]
            [challenge-api.responses :as responses]))

(defn post-handler [{{:keys [persistence headers]} :request}]
  (let [auth (auth/extract-basic headers)
        client (p/get-client-by-email-pass persistence (:email auth) (:pass auth))]

    (when (empty? client)
      (throw (ex-info (:login-incorrect-error responses/messages) {:type   :business-rules
                                                                   :status http-status/unauthorized})))
    {:status   http-status/ok
     :response (assoc (responses/message :login-success client)
                 :JWT (auth/get-token client (get-in persistence [:client-db :secret-jwt])))}))
