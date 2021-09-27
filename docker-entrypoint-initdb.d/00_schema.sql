CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    role     TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE tokens
(
    token    TEXT PRIMARY KEY,
    "userId" BIGINT      NOT NULL REFERENCES users,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE reset_codes
(
    id       BIGSERIAL PRIMARY KEY,
    code     TEXT        NOT NULL UNIQUE,
    username TEXT        NOT NULL,
    password TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT FALSE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE registration_attempts
(
    id       BIGSERIAL   PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE login_attempts
(
    id       BIGSERIAL   PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE cards
(
    id        BIGSERIAL PRIMARY KEY,
    "ownerId" BIGINT  NOT NULL REFERENCES users,
    number    TEXT    NOT NULL,
    balance   BIGINT  NOT NULL DEFAULT 0,
    active    BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE news
(
    id        BIGSERIAL PRIMARY KEY,
    title     TEXT      NOT NULL UNIQUE,
    text      TEXT      NOT NULL UNIQUE,
    created timestamptz NOT NULL DEFAULT current_timestamp
);
