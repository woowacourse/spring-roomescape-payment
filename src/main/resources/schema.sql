CREATE TABLE IF NOT EXISTS reservation_time
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    start_at VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation_theme
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    thumbnail VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation_item
(
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    time_id  BIGINT       NOT NULL,
    theme_id BIGINT       NOT NULL,
    date                 VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time(id),
    FOREIGN KEY (theme_id) REFERENCES reservation_theme(id)
);


CREATE TABLE IF NOT EXISTS member
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    role VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    session_id VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    member_id BIGINT,
    reservation_item_id BIGINT,
    reservation_status VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (reservation_item_id) REFERENCES reservation_item (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS payment
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    reservation_id BIGINT       NOT NULL,
    payment_key    VARCHAR(255) NOT NULL,
    amount         int          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (reservation_id) REFERENCES reservation (id)
);
