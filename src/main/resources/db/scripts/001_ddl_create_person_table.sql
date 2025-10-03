CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    login VARCHAR UNIQUE,
    password VARCHAR
);