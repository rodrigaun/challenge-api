(ns challenge-api.routes
  (:require [challenge-api.responses :as responses]
            [challenge-api.handlers.login :as login]
            [challenge-api.handlers.client :as client]
            [challenge-api.handlers.product :as product]
            [challenge-api.handlers.favorite :as favorite]
            [challenge-api.components.postgres.persistence :as persistence]
            [challenge-api.components.http-server.router :as r]
            [cadastro-de-pessoa.cnpj :as cnpj]
            [integrant.core :as ig]))

(def fields-validation
  {:name  {string? :invalid-format}
   :email {string? :invalid-format}})

(def fields-parse
  {:merchant     cnpj/format
   :conciliatory cnpj/format
   :cnpj         cnpj/format})

(def request-definition
  {:login/post             {:error-message-type :login-error
                            :handler            login/post-handler
                            :required-fields    {:path-params []}}
   :client/get             {:error-message-type :client-consult-error
                            :handler            client/get-handler
                            :required-fields    {:path-params []}}
   :client/post            {:error-message-type :client-create-error
                            :handler            client/post-handler
                            :required-fields    {:body-params [:name :email]}}
   :client/put             {:error-message-type :client-update-error
                            :handler            client/put-handler
                            :required-fields    {:body-params [:name :email]}}
   :client/delete          {:error-message-type :client-delete-error
                            :handler            client/delete-handler
                            :required-fields    {:path-params [:id]}}
   :client/get-by-id       {:error-message-type :client-consult-error
                            :handler            client/get-by-id-handler
                            :required-fields    {:path-params [:id]}}
   :product/get            {:error-message-type :product-consult-error
                            :handler            product/get-handler
                            :required-fields    {:path-params []}}
   :product/get-by-page    {:error-message-type :product-consult-error
                            :handler            product/get-by-page-handler
                            :required-fields    {:path-params []}}
   :product/post           {:error-message-type :product-create-error
                            :handler            product/post-handler
                            :required-fields    {:body-params [:price :image :brand :title :reviewscore]}}
   :product/put            {:error-message-type :product-update-error
                            :handler            product/put-handler
                            :required-fields    {:body-params [:price :image :brand :title :reviewscore]}}
   :product/delete         {:error-message-type :product-delete-error
                            :handler            product/delete-handler
                            :required-fields    {:path-params [:id]}}
   :product/get-by-id      {:error-message-type :product-consult-error
                            :handler            product/get-by-id-handler
                            :required-fields    {:path-params [:id]}}
   :favorite/get-by-client {:error-message-type :favorite-consult-error
                            :handler            favorite/get-by-client-handler
                            :required-fields    {:path-params [:id]}}
   :favorite/post          {:error-message-type :favorite-create-error
                            :handler            favorite/post-handler
                            :required-fields    {:path-params [:id]
                                                 :body-params [:id-product]}}
   :favorite/delete        {:error-message-type :favorite-delete-error
                            :handler            favorite/delete-handler
                            :required-fields    {:path-params [:id]
                                                 :body-params [:id-product]}}})

(defn router [request-type]
  (let [params {:fields-validation  fields-validation
                :request-definition request-definition
                :fields-parse       fields-parse
                :messages           responses/messages}]
    (partial r/route-manager params request-type)))

(def login-routes
  ["/login"
   ["" {:post {:tags    ["Challenge API - Login"]
               :sumary  "Login"
               :handler (router :login/post)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}}]])

(def client-routes
  ["/client"
   ["" {:get  {:tags    ["Challenge API - Cliente"]
               :sumary  "Consulta de cliente"
               :handler (router :client/get)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}
        :post {:tags    ["Challenge API - Cliente"]
               :sumary  "Cadastro de cliente"
               :handler (router :client/post)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}}]
   ["/:id" {:get    {:tags    ["Challenge API - Cliente"]
                     :sumary  "Consulta de cliente por ID"
                     :handler (router :client/get-by-id)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :put    {:tags    ["Challenge API - Cliente"]
                     :sumary  "Atualização de cliente"
                     :handler (router :client/put)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :delete {:tags    ["Challenge API - Cliente"]
                     :sumary  "Exclusão de cliente"
                     :handler (router :client/delete)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}}]])

(def product-routes
  ["/product"
   ["" {:get  {:tags    ["Challenge API - Produto"]
               :sumary  "Consulta de produto"
               :handler (router :product/get)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}
        :post {:tags    ["Challenge API - Produto"]
               :sumary  "Cadastro de produto"
               :handler (router :product/post)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}}]
   ["/" {:get {:tags    ["Challenge API - Produto"]
               :sumary  "Consulta de produto por Pagina"
               :handler (router :product/get-by-page)
               :context {:persistence (ig/ref ::persistence/data-persistence)}}}]
   ["/:id/" {:get    {:tags    ["Challenge API - Produto"]
                     :sumary  "Consulta de produto por ID"
                     :handler (router :product/get-by-id)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :put    {:tags    ["Challenge API - Produto"]
                     :sumary  "Atualização de produto"
                     :handler (router :product/put)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :delete {:tags    ["Challenge API - Produto"]
                     :sumary  "Exclusão de produto"
                     :handler (router :product/delete)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}}]])

(def favorite-routes
  ["/favorite"
   ["/:id" {:get    {:tags    ["Challenge API - Lista Favoritos"]
                     :sumary  "Consulta de produtos favoritos do Cliente"
                     :handler (router :favorite/get-by-client)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :post   {:tags    ["Challenge API - Lista Favoritos"]
                     :sumary  "Cadastro de produto em favoritos do Cliente"
                     :handler (router :favorite/post)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}
            :delete {:tags    ["Challenge API - Lista Favoritos"]
                     :sumary  "Exclusão de produto em favoritos do Cliente"
                     :handler (router :favorite/delete)
                     :context {:persistence (ig/ref ::persistence/data-persistence)}}}]])

(def all
  [["/api"
    login-routes
    client-routes
    product-routes
    favorite-routes]])