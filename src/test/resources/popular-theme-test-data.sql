SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE waiting RESTART IDENTITY;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO member(name, email, password, role)
VALUES ('어드민', 'admin@email.com', '1450575459', 'ADMIN'),
       ('테드', 'test1@email.com', '1450575459', 'USER'),
       ('종리', 'test2@email.com', '1450575459', 'USER'),
       ('범블비', 'test3@email.com', '1450575459', 'USER'),
       ('제이', 'test4@email.com', '1450575459', 'USER');

INSERT INTO theme (theme_name, description, thumbnail)
VALUES ('테마1', '테마1 설명 설명 설명', 'thumbnail1.jpg'),
       ('테마2', '테마2 설명 설명 설명', 'thumbnail2.jpg'),
       ('테마3', '테마3 설명 설명 설명', 'thumbnail3.jpg'),
       ('테마4', '테마4 설명 설명 설명', 'thumbnail4.jpg'),
       ('테마5', '테마5 설명 설명 설명', 'thumbnail5.jpg'),
       ('테마6', '테마6 설명 설명 설명', 'thumbnail6.jpg'),
       ('테마7', '테마7 설명 설명 설명', 'thumbnail7.jpg'),
       ('테마8', '테마8 설명 설명 설명', 'thumbnail8.jpg'),
       ('테마9', '테마9 설명 설명 설명', 'thumbnail9.jpg'),
       ('테마10', '테마10 설명 설명 설명', 'thumbnail10.jpg'),
       ('테마11', '테마11 설명 설명 설명', 'thumbnail11.jpg'),
       ('테마12', '테마12 설명 설명 설명', 'thumbnail12.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('10:00'),
       ('12:00'),
       ('14:00');

INSERT INTO payment (order_id, payment_key, amount)
VALUES ('oderId1', 'paymentKey1', 1000),
       ('oderId2', 'paymentKey2', 1000),
       ('oderId3', 'paymentKey3', 1000),
       ('oderId4', 'paymentKey4', 1000),
       ('oderId5', 'paymentKey5', 1000),
       ('oderId6', 'paymentKey6', 1000),
       ('oderId7', 'paymentKey7', 1000),
       ('oderId8', 'paymentKey8', 1000),
       ('oderId9', 'paymentKey9', 1000),
       ('oderId10', 'paymentKey10', 1000),
       ('oderId11', 'paymentKey11', 1000),
       ('oderId12', 'paymentKey12', 1000),
       ('oderId13', 'paymentKey13', 1000),
       ('oderId14', 'paymentKey14', 1000),
       ('oderId15', 'paymentKey15', 1000),
       ('oderId16', 'paymentKey16', 1000),
       ('oderId17', 'paymentKey17', 1000),
       ('oderId18', 'paymentKey18', 1000),
       ('oderId19', 'paymentKey19', 1000),
       ('oderId20', 'paymentKey20', 1000),
       ('oderId21', 'paymentKey21', 1000);

INSERT INTO reservation (date, time_id, theme_id, member_id, status, payment_id)
VALUES (CURRENT_DATE() - INTERVAL '1' DAY, 1, 1, 2, 'RESERVED', 1),
       (CURRENT_DATE() - INTERVAL '3' DAY, 1, 1, 2, 'RESERVED', 2),
       (CURRENT_DATE() - INTERVAL '5' DAY, 1, 1, 2, 'RESERVED', 3),
       (CURRENT_DATE() - INTERVAL '7' DAY, 1, 1, 2, 'RESERVED', 4),

       (CURRENT_DATE() - INTERVAL '1' DAY, 1, 2, 3, 'RESERVED', 5),
       (CURRENT_DATE() - INTERVAL '3' DAY, 1, 2, 3, 'RESERVED', 6),
       (CURRENT_DATE() - INTERVAL '7' DAY, 1, 2, 3, 'RESERVED', 7),

       (CURRENT_DATE() - INTERVAL '1' DAY, 1, 3, 4, 'RESERVED', 8),
       (CURRENT_DATE() - INTERVAL '2' DAY, 1, 4, 4, 'RESERVED', 9),
       (CURRENT_DATE() - INTERVAL '3' DAY, 1, 5, 4, 'RESERVED', 10),
       (CURRENT_DATE() - INTERVAL '4' DAY, 1, 6, 4, 'RESERVED', 11),
       (CURRENT_DATE() - INTERVAL '5' DAY, 1, 7, 4, 'RESERVED', 12),
       (CURRENT_DATE() - INTERVAL '6' DAY, 1, 8, 4, 'RESERVED', 13),
       (CURRENT_DATE() - INTERVAL '7' DAY, 1, 9, 4, 'RESERVED', 14),
       (CURRENT_DATE() - INTERVAL '7' DAY, 2, 10, 4, 'RESERVED', 15),
       (CURRENT_DATE() - INTERVAL '7' DAY, 3, 11, 4, 'RESERVED', 16),

       (CURRENT_DATE() - INTERVAL '8' DAY, 1, 2, 5, 'RESERVED', 17),
       (CURRENT_DATE() - INTERVAL '8' DAY, 2, 2, 5, 'RESERVED', 18),
       (CURRENT_DATE() - INTERVAL '9' DAY, 1, 12, 5, 'RESERVED', 19),
       (CURRENT_DATE() - INTERVAL '9' DAY, 2, 12, 5, 'RESERVED', 20),
       (CURRENT_DATE() - INTERVAL '9' DAY, 3, 12, 5, 'RESERVED', 21);
