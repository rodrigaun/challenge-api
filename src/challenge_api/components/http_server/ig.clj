(ns challenge-api.components.http-server.ig
  (:require [challenge-api.components.http-server.server :as server]
            [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [io.pedestal.http :as pedestal.http]))

(s/def ::port pos-int?)
(s/def ::config-server (s/keys :req-un [::port]))
(s/def ::url string?)
(s/def ::path string?)
(s/def ::config map?)
(s/def ::swagger-handler-info (s/keys :req [::url ::path ::config]))
(s/def ::routes vector?)

(derive ::server :duct/daemon)

(defmethod ig/pre-init-spec ::server
  [_]
  (s/keys :req-un [::config-server ::routes]))

(defmethod ig/init-key ::server
  [_ config]
  (pedestal.http/start (server/create-server config)))

(defmethod ig/halt-key! ::server
  [_ service]
  (pedestal.http/stop service))