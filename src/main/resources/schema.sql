CREATE TABLE IF NOT EXISTS member
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL UNIQUE CHECK (email like '%@%'),
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    thumbnail   VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation_time
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    start_at VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS payment
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    amount       INT          NOT NULL,
    payment_key  VARCHAR(255) NOT NULL,
    order_id     VARCHAR(255) NOT NULL,
    requested_at VARCHAR(255) NOT NULL,
    approved_at  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    member_id           BIGINT       NOT NULL,
    theme_id            BIGINT       NOT NULL,
    date                VARCHAR(255) NOT NULL,
    reservation_time_id BIGINT       NOT NULL,
    status              VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    payment_id          BIGINT UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    FOREIGN KEY (reservation_time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (payment_id) REFERENCES payment (id)
);
