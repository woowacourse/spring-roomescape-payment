
CREATE TABLE IF NOT EXISTS reservation_time
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    start_at TIME   NOT NULL,
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

CREATE TABLE IF NOT EXISTS member
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS reservation_slot
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    date     DATE   NOT NULL,
    reservation_time_id  BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,                             -- 컬럼 추가
    PRIMARY KEY (id),
    FOREIGN KEY (reservation_time_id) REFERENCES reservation_time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id) -- 외래키 추가
    );

CREATE TABLE IF NOT EXISTS payment
(
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    order_id        VARCHAR(255)    NOT NULL,
    payment_key     VARCHAR(255)    NOT NULL,
    total_amount    BIGINT  NOT NULL,
    payment_method          VARCHAR(255)    NOT NULL,
    requested_at    DATETIME    NOT NULL,
    approved_at     DATETIME    NOT NULL,
    status          VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservation
(
    id             BIGINT NOT NULL AUTO_INCREMENT,
    member_id      BIGINT NOT NULL,
    reservation_slot_id BIGINT NOT NULL,
    payment_id     BIGINT,
    created_at DATETIME NOT NULL,
    status VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (reservation_slot_id) REFERENCES reservation_slot (id),
    FOREIGN KEY (payment_id) REFERENCES payment (id),
    PRIMARY KEY (id)
);
