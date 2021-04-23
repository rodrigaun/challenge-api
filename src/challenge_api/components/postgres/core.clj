(ns challenge-api.components.postgres.core
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [medley.core :as m]
            [challenge-api.components.postgres.protocols :as p]
            [clojure.string :as str])
  (:import (io.zonky.test.db.postgres.embedded EmbeddedPostgres)
           (java.io Closeable)
           (java.sql DriverManager)
           (org.postgresql.jdbc PgArray)))

(defn- generate-url [host port database username password]
  (str "jdbc:postgresql://" host ":" port "/" database "?user=" username "&password=" password))

(defn new-connection [{:keys                       [host port database]
                       {:keys [username password]} :credentials} auto-commit?]
  (let [conn (-> (generate-url host port database username password)
                 (DriverManager/getConnection))]
    (.setAutoCommit conn auto-commit?)
    conn))

(defn- add-params! [prepared-query params]
  (dotimes [i (count params)]
    (if (instance? PgArray (get params i))
      (.setArray prepared-query (inc i) (get params i))
      (.setObject prepared-query (inc i) (get params i)))))

(defn executeInstruction [connection {:keys [query params] :or {params []}}]
  (let [prepared-query (.prepareStatement connection query)]
    (add-params! prepared-query params)
    (.executeUpdate prepared-query)))

(defn- result-set->edn [result-set parse-column]
  (let [records (atom [])]
    (while (.next result-set)
      (let [record (atom {})]
        (dotimes [n (.getColumnCount (.getMetaData result-set))]
          (swap! record assoc (parse-column (.getColumnName (.getMetaData result-set) (inc n))) (.getObject result-set (inc n))))
        (swap! records conj @record)))
    @records))

(defn executeQueryInstruction [connection {:keys [query params] :or {params []}} parse-column]
  (let [prepared-query (.prepareStatement connection query)]
    (add-params! prepared-query params)
    (-> (.executeQuery prepared-query)
        (result-set->edn parse-column))))

(defn init [configs parse-column]
  (reify
    p/PostgresClient
    (connection [_ auto-commit?] (new-connection configs auto-commit?))
    (commit [_ connection] (.commit connection))
    (rollback [_ connection] (.rollback connection))
    (execute [_ connection query-params] (executeInstruction connection query-params))
    (executeQuery [_ connection query-params] (executeQueryInstruction connection query-params parse-column))))

(def query-tables "SELECT tablename FROM pg_tables where schemaname = 'public';")

(defn ever-created? [conn]
  (let [prepared-query (.prepareStatement conn query-tables)
        result (-> (.executeQuery prepared-query)
                   (result-set->edn identity))]
    (or
      (str/includes? (str result) "client")
      (str/includes? (str result) "product")
      (str/includes? (str result) "favorite"))))

(defn content-file [query-sql-file]
  (-> query-sql-file
      first
      str
      slurp))

(defn migration! [client {:keys [query-sql-file]}]
  (with-open [conn (p/connection client false)]
    (try
      (when-not (ever-created? conn)
        (let [prepared-query (.prepareStatement conn (content-file query-sql-file))]
          (.executeUpdate prepared-query)))
      (.commit conn)
      (catch Exception e
        (.rollback conn)
        (throw e)))))