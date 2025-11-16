-- Initial schema creation for User and Patient tables

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active_account BOOLEAN NOT NULL,
    locked_account BOOLEAN NOT NULL,
    credentials_non_expired BOOLEAN NOT NULL,
    enabled_account BOOLEAN NOT NULL,
    authorities VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS patient (
    patient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    birthdate VARCHAR(255) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    address VARCHAR(255),
    phoneNumber VARCHAR(15)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
