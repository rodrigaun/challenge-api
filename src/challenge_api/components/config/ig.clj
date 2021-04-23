(ns challenge-api.components.config.ig
  (:require
    [integrant.core :as ig]
    [challenge-api.components.integrant :as pgo.ig]
    [aero.core :as aero]
    [clojure.java.io :as io]
    [clojure.spec.alpha :as s]))

(derive ::path ::pgo.ig/const)
(derive ::env ::pgo.ig/const)

(defn read-config-file [env path]
  (aero/read-config (io/resource path)
                    {:profile  env
                     :resolver aero/resource-resolver}))

(defn read-config-keys [env path ks]
  (-> (read-config-file env path) (select-keys ks)))

(defn read-config-key [env path k]
  (-> (read-config-file env path) (get k)))

(defmethod ig/init-key ::config
  [_ {:keys [env path]
      ks    :keys
      k     :key}]
  (cond (some? ks) (read-config-keys env path ks)
        (some? k) (read-config-key env path k)))

(defn both-keys?
  [x]
  (and (contains? x :keys) (contains? x :key)))

(s/def ::key keyword?)
(s/def ::keys (s/coll-of ::key :kind set?))

(defmethod ig/pre-init-spec ::config
  [_]
  (s/and
    (s/keys :req-un [(or ::key ::keys)])
    (complement both-keys?)))