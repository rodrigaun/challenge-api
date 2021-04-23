(ns user
  (:require
    [challenge-api.system :as system]
    [integrant.repl :as ig.repl]
    [integrant.core :as ig]))

(ig.repl/set-prep! #(ig/prep (system/config :dev)))

(defn go []
  (ig.repl/go))

(defn halt []
  (ig.repl/halt))

(defn reset []
  (ig.repl/reset))

(comment
  (go)
  (halt))

