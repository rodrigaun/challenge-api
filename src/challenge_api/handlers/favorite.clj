(ns challenge-api.handlers.favorite
  (:require [ring.util.http-status :as http-status]
            [challenge-api.components.postgres.persistence :as p]
            [challenge-api.responses :as responses]
            [challenge-api.modules.auth :as auth]))

(defn get-by-client-handler [{{:keys [id]}                  :path-params
                              {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (let [product (when (some? id)
                  (p/get-favorite-by-client persistence (Long/valueOf id)))]
    (when (empty? product)
      (throw (ex-info (:favorite-empty-error responses/messages)
                      {:type   :business-rules
                       :status http-status/not-found})))
    {:status   http-status/ok
     :response product}))

(defn post-handler [{{:keys [id]}                  :path-params
                     {:keys [id-product]}          :body
                     {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id-product)
             (empty? (p/get-product-by-id persistence id-product)))
    (throw (ex-info (:product-not-found-error responses/messages) {:type   :business-rules
                                                                   :status http-status/not-found})))

  (when (and (some? id)
             (not (empty? (p/get-favorite persistence (Long/valueOf id) id-product))))
    (throw (ex-info (:favorite-found-error responses/messages) {:type   :business-rules
                                                                :status http-status/conflict})))
  (let [favorite (p/post-favorite persistence (Long/valueOf id) id-product)]
    {:status   http-status/created
     :response (responses/message :favorite-created favorite)}))

(defn delete-handler [{{:keys [id]}                  :path-params
                       {:keys [id-product]}          :body
                       {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id)
             (empty? (p/get-favorite persistence (Long/valueOf id) id-product)))
    (throw (ex-info (:favorite-not-found-error responses/messages) {:type   :business-rules
                                                                    :status http-status/not-found})))
  (let [product (p/delete-favorite persistence (Long/valueOf id) id-product)]
    {:status   http-status/ok
     :response (responses/message :favorite-deleted product)}))