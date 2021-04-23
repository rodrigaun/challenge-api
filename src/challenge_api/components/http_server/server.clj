(ns challenge-api.components.http-server.server
  (:require [clojure.walk :as walk]
            [io.pedestal.http :as pedestal.http]
            [io.pedestal.interceptor :as pedestal.interceptor]
            [muuntaja.interceptor :as muuntaja.interceptor]
            [muuntaja.core :as muuntaja]
            [muuntaja.format.core :as muuntaja.format]
            [muuntaja.format.cheshire :as muuntaja.cheshire]
            [medley.core :as medley]
            [reitit.http :as reitit.http]
            [reitit.pedestal :as reitit.pedestal]
            [reitit.ring :as reitit.ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.http.interceptors.exception :as reitit.interceptors.ex]
            [ring.util.http-status :as http-status]))

(defn json-format
  ([] (json-format {}))
  ([{:keys [decoder-opts encoder-opts]}]
   (muuntaja.format/map->Format
     {:name "application/json"
      :decoder [muuntaja.cheshire/decoder decoder-opts]
      :encoder [muuntaja.cheshire/encoder encoder-opts]})))

(defn add-context-interceptor [{:keys [context] :as route}]
  (-> route
      (dissoc :context)
      (update :interceptors
              (fnil conj [])
              {:name ::context-interceptor
               :enter #(update % :request merge context)})))

(defn add-context-interceptors [routes]
  (walk/postwalk (fn [x]
                   (if (and (map? x) (contains? x :context))
                     (add-context-interceptor x)
                     x))
                 routes))

(defn error-response-handler [message & _]
  {:status http-status/internal-server-error
   :body {:message message}})

(def error-handlers
  (merge
    reitit.interceptors.ex/default-handlers
    {clojure.lang.ExceptionInfo (partial error-response-handler "Erro no servidor")}))

(defn create-router [{:keys [routes toggle]}]
  (reitit.http/router
    (add-context-interceptors routes)
    (medley/filter-vals some? {:data {:toggle toggle
                                      :interceptors [swagger/swagger-feature
                                                     (reitit.interceptors.ex/exception-interceptor error-handlers)]}})))

(defn create-router-interceptor
  [{:keys [router]} swagger-handler-info]
  (let [info (if (some? swagger-handler-info)
               (swagger-ui/create-swagger-ui-handler swagger-handler-info)
               (swagger-ui/create-swagger-ui-handler))]
    (reitit.pedestal/routing-interceptor
      router
      (reitit.ring/routes
        info
        (reitit.ring/create-default-handler))
      {:interceptors [(muuntaja.interceptor/format-interceptor
                        (-> muuntaja/default-options
                            (assoc-in [:formats "application/json"] (json-format))))]})))

(defn create-service-map [{:keys [config-server]}]
  (->> config-server
       (medley/map-keys #(keyword "io.pedestal.http" (name %)))
       (into {})
       (medley/deep-merge {::pedestal.http/type :jetty
                           ::pedestal.http/allowed-origins (constantly true)
                           ::pedestal.http/host "0.0.0.0"
                           ::pedestal.http/join? false
                           ::pedestal.http/routes []
                           ::pedestal.http/secure-headers {:content-security-policy-settings
                                                           {:default-src "'self'"
                                                            :style-src "'self' 'unsafe-inline'"
                                                            :script-src "'self' 'unsafe-inline'"}}})))

(defn create-server [{:keys [env swagger-handler-info] :as server-config}]
  (let [router             (create-router server-config)
        router-interceptor (create-router-interceptor {:router router} swagger-handler-info)]
    (-> (create-service-map server-config)
        (assoc :env env)
        (assoc :reitit/router router)
        (pedestal.http/default-interceptors)
        (reitit.pedestal/replace-last-interceptor router-interceptor)
        (pedestal.http/dev-interceptors)
        (pedestal.http/create-server))))