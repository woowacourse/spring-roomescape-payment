DROP TABLE IF EXISTS theme cascade;
DROP TABLE IF EXISTS reservation_time cascade;
DROP TABLE IF EXISTS member cascade;
DROP TABLE IF EXISTS reservation cascade;
DROP TABLE IF EXISTS reservation_waiting cascade;
DROP TABLE IF EXISTS payment cascade;

CREATE TABLE theme (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    thumbnail VARCHAR(512),
    create_at TIMESTAMP NOT NULL
);
CREATE TABLE reservation_time (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_at TIME,
    create_at TIMESTAMP NOT NULL
);

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('MEMBER', 'ADMIN') NOT NULL,
    create_at TIMESTAMP NOT NULL
);
CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    time_id BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,
    reservation_member_id BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (time_id) REFERENCES reservation_time(id),
    FOREIGN KEY (theme_id) REFERENCES theme(id),
    FOREIGN KEY (reservation_member_id) REFERENCES member(id)
);
create table reservation_waiting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    waiting_member_id BIGINT NOT NULL,
    create_at TIMESTAMP NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id),
    FOREIGN KEY (waiting_member_id) REFERENCES member(id)
);
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_key VARCHAR(255),
    amount BIGINT NOT NULL,
    reservation_id BIGINT,
    create_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id)
);
