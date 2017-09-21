-- Create the products table if not present
CREATE TABLE IF NOT EXISTS products (
  id        INTEGER IDENTITY,
  name      VARCHAR(40) NOT NULL
);
INSERT INTO products (name) values ('Apple');
INSERT INTO products (name) values ('Orange');
INSERT INTO products (name) values ('Pear');