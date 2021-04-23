(ns challenge-api.handlers.client
  (:require [ring.util.http-status :as http-status]
            [challenge-api.components.postgres.persistence :as p]
            [challenge-api.modules.auth :as auth]
            [challenge-api.responses :as responses]))

(defn get-handler [{{:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)
  (let [clients (p/get-client persistence)]
    {:status   http-status/ok
     :response clients}))

(defn post-handler [{{:keys [name email]}          :body
                     {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? email)
             (some? (p/get-client-by-email persistence email)))
    (throw (ex-info (:client-exist-error responses/messages) {:type   :business-rules
                                                              :status http-status/conflict})))

  (let [{:keys [id]} (p/post-client persistence name email "1234")]
    {:status   http-status/created
     :response (responses/message :client-created {:id id})}))

(defn put-handler [{{:keys [id]}          :path-params
                    {:keys [name email]}  :body
                    {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id)
             (empty? (p/get-client-by-id persistence (Long/valueOf id))))
    (throw (ex-info (:client-not-found-error responses/messages) {:type   :business-rules
                                                                  :status http-status/not-found})))

  (let [client (p/update-client persistence (Long/valueOf id) name email)]
    {:status   http-status/ok
     :response (responses/message :client-updated client)}))

(defn delete-handler [{{:keys [id]}          :path-params
                       {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id)
             (empty? (p/get-client-by-id persistence (Long/valueOf id))))
    (throw (ex-info (:client-not-found-error responses/messages) {:type   :business-rules
                                                                  :status http-status/not-found})))
  (let [client (p/delete-client persistence (Long/valueOf id))]
    {:status   http-status/ok
     :response (responses/message :client-deleted client)}))

(defn get-by-id-handler [{{:keys [id]}          :path-params
                          {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (let [client (when (some? id)
                 (p/get-client-by-id persistence (Long/valueOf id)))]

    (when (empty? client)
      (throw (ex-info (:client-not-found-error responses/messages)
                      {:type   :business-rules
                       :status http-status/not-found})))

    {:status   http-status/ok
     :response client}))
