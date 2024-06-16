CREATE DATABASE roomescape DEFAULT CHARACTER SET utf8m4 COLLATE utf8_general_ci;
USE roomescape;

CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT,
                        email VARCHAR(255),
                        name VARCHAR(255),
                        password VARCHAR(255),
                        role ENUM ('MEMBER', 'ADMIN'),
                        PRIMARY KEY (id)
);

CREATE TABLE theme (
                       id BIGINT AUTO_INCREMENT,
                       description VARCHAR(255),
                       name VARCHAR(255),
                       thumbnail VARCHAR(255),
                       PRIMARY KEY (id)
);

CREATE TABLE reservation_time (
                                  id BIGINT AUTO_INCREMENT,
                                  start_at TIME,
                                  PRIMARY KEY (id)
);

CREATE TABLE reservation (
                             id BIGINT AUTO_INCREMENT,
                             date DATE,
                             member_id BIGINT,
                             theme_id BIGINT,
                             time_id BIGINT,
                             deleted BOOLEAN default false,
                             PRIMARY KEY (id),
                             FOREIGN KEY (member_id) REFERENCES member(id),
                             FOREIGN KEY (theme_id) REFERENCES theme(id),
                             FOREIGN KEY (time_id) REFERENCES reservation_time(id)
);

CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT,
                         payment_key VARCHAR(255),
                         amount DECIMAL(15, 2),
                         reservation_id BIGINT,
                         PRIMARY KEY (id),
                         FOREIGN KEY (reservation_id) REFERENCES reservation(id)
);

CREATE TABLE waiting (
                         id BIGINT AUTO_INCREMENT,
                         date DATE,
                         member_id BIGINT,
                         theme_id BIGINT,
                         time_id BIGINT,
                         deleted BOOLEAN default false,
                         PRIMARY KEY (id),
                         FOREIGN KEY (member_id) REFERENCES member(id),
                         FOREIGN KEY (theme_id) REFERENCES theme(id),
                         FOREIGN KEY (time_id) REFERENCES reservation_time(id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON roomescape.* TO 'user'@'%';
FLUSH PRIVILEGES;

INSERT INTO reservation_time (start_at) VALUES ('10:00');
INSERT INTO reservation_time (start_at) VALUES ('11:00');
INSERT INTO reservation_time (start_at) VALUES ('12:00');
INSERT INTO reservation_time (start_at) VALUES ('13:00');
INSERT INTO reservation_time (start_at) VALUES ('14:00');
INSERT INTO reservation_time (start_at) VALUES ('15:00');

INSERT INTO theme (name, description, thumbnail) VALUES ('에버', '공포', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('배키', '스릴러', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('네오', '공포', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('리사', '판타지', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('썬', '드라마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('포비', '스릴러', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('구구', '판타지', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('토미', '드라마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('브리', '드라마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('솔라', '공포', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('왼손', '판타지', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO member (name, email, role, password) VALUES ('썬', 'sun@email.com', 'MEMBER', '1234');
INSERT INTO member (name, email, role, password) VALUES ('배키', 'dmsgml@email.com', 'MEMBER', '1111');
INSERT INTO member (name, email, role, password) VALUES ('포비', 'pobi@email.com', 'ADMIN', '2222');

INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-09', 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-07', 2, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-08', 4, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-09', 4, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-09', 3, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id) VALUES ('2024-05-09', 1, 3, 2);

GRANT SELECT,INSERT,UPDATE,DELETE ON roomescape.* TO 'user'@'%';

FLUSH PRIVILEGES;