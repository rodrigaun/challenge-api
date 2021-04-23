(ns challenge-api.handlers.product
  (:require [ring.util.http-status :as http-status]
            [challenge-api.components.postgres.persistence :as p]
            [challenge-api.responses :as responses]
            [challenge-api.modules.auth :as auth]))

(defn get-handler [{{:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (let [products (p/get-product persistence)]
    {:status   http-status/ok
     :response products}))

(defn get-by-page-handler [{{:keys [page itens]}          :query-params
                            {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (let [page (Long/valueOf page)
        itens (Long/valueOf (or itens 5))
        products (p/get-product-by-page persistence page itens)]
    {:status   http-status/ok
     :response products}))

(defn post-handler [{{:keys [price image brand title reviewscore]} :body
                     {:keys [persistence headers]}                 :request}]
  (auth/validate-token headers persistence)

  (let [{:keys [id]} (p/post-product persistence price image brand title reviewscore)]
    {:status   http-status/created
     :response (responses/message :product-created {:id id})}))

(defn put-handler [{{:keys [id]}                                  :path-params
                    {:keys [price image brand title reviewscore]} :body
                    {:keys [persistence headers]}                 :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id)
             (empty? (p/get-product-by-id persistence (Long/valueOf id))))
    (throw (ex-info (:product-not-found-error responses/messages) {:type   :business-rules
                                                                   :status http-status/not-found})))

  (let [product (p/update-product persistence (Long/valueOf id) price image brand title reviewscore)]
    {:status   http-status/ok
     :response (responses/message :product-updated product)}))

(defn delete-handler [{{:keys [id]}                  :path-params
                       {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (when (and (some? id)
             (empty? (p/get-product-by-id persistence (Long/valueOf id))))
    (throw (ex-info (:product-not-found-error responses/messages) {:type   :business-rules
                                                                   :status http-status/not-found})))
  (let [product (p/delete-product persistence (Long/valueOf id))]
    {:status   http-status/ok
     :response (responses/message :product-deleted product)}))

(defn get-by-id-handler [{{:keys [id]}                  :path-params
                          {:keys [persistence headers]} :request}]
  (auth/validate-token headers persistence)

  (let [product (when (some? id)
                  (p/get-product-by-id persistence (Long/valueOf id)))]

    (when (empty? product)
      (throw (ex-info (:product-not-found-error responses/messages)
                      {:type   :business-rules
                       :status http-status/not-found})))

    {:status   http-status/ok
     :response product}))
