-- Eliminar base de dades si existeix
DROP DATABASE IF EXISTS tpv_botiga;

-- Crear base de dades
CREATE DATABASE tpv_botiga;
USE tpv_botiga;

-- =========================
-- TAULA ARTICLES
-- =========================
CREATE TABLE articles (
    id INT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    familia ENUM('camisa', 'pantaló') NOT NULL,

    talla_coll INT,
    amplada_pit INT,

    talla_cintura INT,
    llargada_camal INT,

    preu_base DECIMAL(10,2) NOT NULL,
    iva INT NOT NULL,
    stock INT NOT NULL,

    CHECK (iva BETWEEN 4 AND 21),
    CHECK (stock >= 0),

    CHECK (
        (familia = 'camisa' AND talla_coll BETWEEN 36 AND 52 AND amplada_pit BETWEEN 10 AND 15)
        OR
        (familia = 'pantaló' AND talla_cintura BETWEEN 24 AND 56 AND llargada_camal BETWEEN 32 AND 46)
    )
);

-- =========================
-- TAULA CLIENTS
-- =========================
CREATE TABLE clients (
    dni VARCHAR(10) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefon VARCHAR(20)
);

-- Client genèric
INSERT INTO clients (dni, nom, email, telefon)
VALUES ('000', 'Client Genèric', NULL, NULL);

-- =========================
-- TAULA TIQUETS
-- =========================
CREATE TABLE tiquets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_compra DATE NOT NULL,
    dni_client VARCHAR(10) NOT NULL,

    total_base DECIMAL(10,2) NOT NULL,
    total_iva DECIMAL(10,2) NOT NULL,
    total_final DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (dni_client) REFERENCES clients(dni)
);

-- =========================
-- TAULA LINIES DE FACTURA
-- =========================
CREATE TABLE linies_factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tiquet INT NOT NULL,
    id_article INT NOT NULL,

    quantitat INT NOT NULL,
    preu_base DECIMAL(10,2) NOT NULL,
    iva INT NOT NULL,
    preu_final DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_tiquet) REFERENCES tiquets(id) ON DELETE CASCADE,
    FOREIGN KEY (id_article) REFERENCES articles(id),

    CHECK (quantitat > 0),
    CHECK (iva BETWEEN 4 AND 21)
);