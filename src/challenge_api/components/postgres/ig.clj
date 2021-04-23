(ns challenge-api.components.postgres.ig
  (:require [integrant.core :as ig]
            [challenge-api.components.postgres.core :as c]))

(defn init-client [{{:keys [migration]} :configs
                    :keys [configs parse-column]
                    :or {parse-column keyword} :as client-config}]
  (let [client (c/init configs parse-column)
        client-component (assoc client-config :client client)]
    (c/migration! client migration)
    client-component))

(defmethod ig/prep-key ::client [_ config]
  config)

(defmethod ig/init-key ::client
  [_ config]
  (init-client config))