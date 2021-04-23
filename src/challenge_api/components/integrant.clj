(ns challenge-api.components.integrant
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            [clojure.java.io :as io]
            [duct.core :as duct]))

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod aero/reader 'ig/refset
  [_ _ value]
  (ig/refset value))

(defn read-config-file [env path]
  (aero/read-config (io/resource path) {:profile env}))

(defn symbol->namespace [sym]
  (or (some-> sym namespace symbol) sym))

(def try-require-symbols-xf
  (comp (filter symbol?)
        (map symbol->namespace)
        (distinct)
        (keep #'ig/try-require)))

(defn depth-first-seq
  ([coll]
   (depth-first-seq identity coll))
  ([xf coll]
   (sequence xf (tree-seq coll? seq coll))))

(defn load-symbol-namespaces [coll]
  (depth-first-seq try-require-symbols-xf coll))

(defn load-namespaces [config]
  (distinct
    (concat (ig/load-namespaces config)
            (load-symbol-namespaces config))))

(defn loading-prep [config]
  (load-namespaces config)
  (ig/prep config))

(defn update-derived [config k f & args]
  (reduce #(apply update %1 %2 f args)
          config
          (keys (ig/find-derived config k))))

(defn exec-config [config ks]
  (-> config (loading-prep) (ig/init ks) (duct/await-daemons)))

(defn main-fn [config-fn]
  (fn [args]
    (let [config (config-fn :prod)
          ks (or (duct/parse-keys args) [:duct/daemon])]
      (exec-config config ks))))