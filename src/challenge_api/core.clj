(ns challenge-api.core
  (:gen-class)
  (:require [challenge-api.components.integrant :as ig]
            [challenge-api.system :as system]))

(defn -main
  [& args]
  ((ig/main-fn system/config) args))