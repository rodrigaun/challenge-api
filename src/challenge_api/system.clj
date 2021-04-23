(ns challenge-api.system
  (:require [challenge-api.components.http-server.ig :as server-ig]
            [challenge-api.components.postgres.ig :as p-ig]
            [integrant.core :as ig]
            [camel-snake-kebab.core :as csk]
            [challenge-api.routes :as routes]
            [challenge-api.components.postgres.database :as db]
            [challenge-api.components.postgres.persistence :as persistence]))

(defn getenv
  ([env-name] (getenv env-name nil))
  ([env-name default-value]
   (or (System/getenv env-name) default-value)))

(defn config [env]
  {::server-ig/server
   {:config-server {:port 3000}
    :routes        routes/all
    :env           env}

   ::p-ig/client
   {:configs      {:migration   {:query-sql-file db/query-sql-file}
                   :host        (getenv "POSTGRES_HOST" "localhost")
                   :port        (getenv "POSTGRES_PORT" "5432")
                   :database    (getenv "POSTGRES_DATABASE" "postgres")
                   :credentials {:username (getenv "POSTGRES_USERNAME" "postgres")
                                 :password (getenv "POSTGRES_PASSWORD" "password")}}
    :secret-jwt   (getenv "SECRET_JWT" "localhost")
    :parse-column csk/->kebab-case-keyword}

   ::persistence/data-persistence
   {:client-db (ig/ref ::p-ig/client)}

   })