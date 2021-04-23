(ns challenge-api.components.postgres.persistence
  (:require [integrant.core :as ig]
            [challenge-api.components.postgres.protocols :as p]
            [challenge-api.components.postgres.database :as database]))

(defn- getting-client [{:keys [client]}]
  (with-open [conn (p/connection client true)]
    (p/executeQuery client conn {:query  database/getting-client
                                 :params []})))

(defn- getting-client-by-id [{:keys [client]} id]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/getting-client-by-id
                                        :params [id]}))))

(defn- getting-client-by-email [{:keys [client]} email]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/getting-client-by-email
                                        :params [email]}))))

(defn- getting-client-by-email-pass [{:keys [client]} email pass]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/getting-client-by-email-pass
                                        :params [email pass]}))))

(defn- posting-client [{:keys [client]} name email pass]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/inserting-client
                                        :params [name email pass]}))))

(defn- updating-client [{:keys [client]} id name email]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/updating-client
                                        :params [name email id]}))))

(defn- deleting-client [{:keys [client]} id]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/deleting-client
                                        :params [id]}))))

(defn- getting-product [{:keys [client]}]
  (with-open [conn (p/connection client true)]
    (p/executeQuery client conn {:query  database/getting-product
                                 :params []})))

(defn- getting-product-by-id [{:keys [client]} id]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/getting-product-by-id
                                        :params [id]}))))

(defn- getting-product-by-page [{:keys [client]} page itens]
  (with-open [conn (p/connection client true)]
    (p/executeQuery client conn {:query  database/getting-product-by-page
                                 :params [itens page itens]})))

(defn- posting-product [{:keys [client]} price image brand title reviewscore]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/inserting-product
                                        :params [price image brand title reviewscore]}))))

(defn- updating-product [{:keys [client]} id price image brand title reviewscore]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/updating-product
                                        :params [price image brand title reviewscore id]}))))

(defn- deleting-product [{:keys [client]} id]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/deleting-product
                                        :params [id]}))))

(defn- getting-favorite-by-client [{:keys [client]} id-client]
  (with-open [conn (p/connection client true)]
    (p/executeQuery client conn {:query  database/getting-favorite-by-client
                                 :params [id-client]})))

(defn- getting-favorite [{:keys [client]} id-client id-product]
  (with-open [conn (p/connection client true)]
    (p/executeQuery client conn {:query  database/getting-favorite
                                 :params [id-client id-product]})))

(defn- posting-favorite [{:keys [client]} id-client id-product]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/inserting-favorite
                                        :params [id-client id-product]}))))

(defn- deleting-favorite [{:keys [client]} id-client id-product]
  (with-open [conn (p/connection client true)]
    (first (p/executeQuery client conn {:query  database/deleting-favorite
                                        :params [id-client id-product]}))))

(defprotocol DataPersistence
  (get-client [this] "")
  (get-client-by-id [this id] "")
  (get-client-by-email [this email] "")
  (get-client-by-email-pass [this email pass] "")
  (post-client [this name email pass] "")
  (update-client [this id name email] "")
  (delete-client [this id] "")
  (get-product [this] "")
  (get-product-by-id [this id] "")
  (get-product-by-page [this page itens] "")
  (post-product [this price image brand title reviewscore] "")
  (update-product [this id price image brand title reviewscore] "")
  (delete-product [this id] "")
  (get-favorite-by-client [this id-client] "")
  (get-favorite [this id-client id-product] "")
  (post-favorite [this id-client id-product] "")
  (delete-favorite [this id-client id-product] ""))

(defrecord PersistenceComponent [client-db]
  DataPersistence
  (get-client [_] (getting-client client-db))
  (get-client-by-id [_ id] (getting-client-by-id client-db id))
  (get-client-by-email [_ email] (getting-client-by-email client-db email))
  (get-client-by-email-pass [_ email pass] (getting-client-by-email-pass client-db email pass))
  (post-client [_ name email pass] (posting-client client-db name email pass))
  (update-client [_ id name email] (updating-client client-db id name email))
  (delete-client [_ id] (deleting-client client-db id))
  (get-product [_] (getting-product client-db))
  (get-product-by-id [_ id] (getting-product-by-id client-db id))
  (get-product-by-page [_ page itens] (getting-product-by-page client-db page itens))
  (post-product [_ price image brand title reviewscore] (posting-product client-db price image brand title reviewscore))
  (update-product [_ id price image brand title reviewscore] (updating-product client-db id price image brand title reviewscore))
  (delete-product [_ id] (deleting-product client-db id))
  (get-favorite-by-client [_ id-client] (getting-favorite-by-client client-db id-client))
  (get-favorite [_ id-client id-product] (getting-favorite client-db id-client id-product))
  (post-favorite [_ id-client id-product] (posting-favorite client-db id-client id-product))
  (delete-favorite [_ id-client id-product] (deleting-favorite client-db id-client id-product)))

(defmethod ig/init-key ::data-persistence
  [_ {:keys [client-db]}]
  (->PersistenceComponent client-db))