CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS client (
  ID serial NOT NULL UNIQUE,
  Email VARCHAR(100) NOT NULL PRIMARY KEY,
  Name VARCHAR(100) NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
  ID serial NOT NULL PRIMARY KEY,
  Price NUMERIC(10,2) NOT NULL,
  Image VARCHAR(200) NOT NULL,
  Brand VARCHAR(100) NOT NULL,
  Title VARCHAR(100) NOT NULL,
  Reviewscore NUMERIC(3,0) NOT NULL,
  UNIQUE (ID)
);

CREATE TABLE IF NOT EXISTS favorite (
  ID serial NOT NULL PRIMARY KEY,
  ID_Client INTEGER NOT NULL,
  ID_Product INTEGER NOT NULL,
  FOREIGN KEY (ID_Client) REFERENCES public.client (ID),
  FOREIGN KEY (ID_Product) REFERENCES public.product (ID)
);

INSERT INTO public.client
    (email, name, password)
VALUES
    ('email-jose@email.com', 'Jose', crypt('1234', gen_salt('bf'))),
    ('email-maria@email.com', 'Maria', crypt('5678', gen_salt('bf'))),
    ('email-pedro@email.com', 'Pedro', crypt('1234', gen_salt('bf'))),
    ('email-julia@email.com', 'Julia', crypt('5678', gen_salt('bf')));

INSERT INTO public.product
    (price, image, brand, title, reviewscore)
VALUES
    (3500, 'product-A.png', 'LG', 'SmartTV 55', 90),
    (1650.10, 'product-B.png', 'LG', 'Smartphone', 40),
    (3400, 'product-C.png', 'LG', 'Lava e Seca', 70),
    (4299, 'product-D.png', 'LG', 'Refrigerador', 62),
    (1900, 'product-E.png', 'LG', 'Notebook', 23),
    (1249, 'product-F.png', 'LG', 'Monitor 29', 87),
    (300, 'product-G.png', 'HP', 'Impressora Laser', 100),
    (700, 'product-H.png', 'HP', 'Multifuncional', 80);

INSERT INTO public.favorite
    (id_client, id_product)
VALUES
    (1, 1),
    (1, 2),
    (1, 3);