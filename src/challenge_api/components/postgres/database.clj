(ns challenge-api.components.postgres.database
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def query-sql-file [(io/resource "query.sql")])


(def getting-client " SELECT id, email, name FROM client")

(def getting-client-by-id " SELECT id, email, name FROM client WHERE id = ?")

(def getting-client-by-email " SELECT id, email, name FROM client WHERE email = ?")

(def getting-client-by-email-pass " SELECT id, email, name FROM client WHERE email = ? AND password = crypt(?, password)")

(def inserting-client " INSERT INTO client (name, email, password) VALUES (?, ?, crypt(?, gen_salt('bf'))) returning id")

(def updating-client " UPDATE client SET name = ?, email = ? WHERE id = ? returning id, email, name")

(def deleting-client " DELETE FROM client WHERE id = ? returning id, email, name")


(def getting-product " SELECT * FROM product")

(def getting-product-by-id " SELECT * FROM product WHERE id = ?")

(def getting-product-by-page " SELECT * FROM product LIMIT ? OFFSET(? - 1) * ?")

(def inserting-product " INSERT INTO product (price, image, brand, title, reviewscore) VALUES (?, ?, ?, ?, ?) returning id")

(def updating-product " UPDATE product SET price = ?, image = ?, brand = ?, title = ?, reviewscore = ? WHERE id = ? returning *")

(def deleting-product " DELETE FROM product WHERE id = ? returning *")


(def getting-favorite-by-client " SELECT p.* FROM favorite f INNER JOIN product p ON p.id = f.id_product WHERE f.id_client = ?")

(def getting-favorite " SELECT * FROM favorite WHERE id_client = ? AND id_product = ?")

(def inserting-favorite " INSERT INTO favorite (id_client, id_product) VALUES (?, ?) returning *")

(def deleting-favorite " DELETE FROM favorite WHERE id_client = ? AND id_product = ? returning *")
