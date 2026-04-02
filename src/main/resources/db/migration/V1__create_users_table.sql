-- src/main/resources/db/migration/V1__create_users_table.sql

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       friend_code INTEGER NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       modified_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX uk_users_email ON users (email);
CREATE UNIQUE INDEX uk_users_username ON users (username);