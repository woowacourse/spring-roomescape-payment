SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO member(name, email, password, role)
VALUES ('어드민', 'admin@email.com', '1450575459', 'ADMIN'),
       ('테드', 'ted@email.com', '1450575459', 'USER'),
       ('종이', 'jonge@email.com', '1450575459', 'USER'),
       ('아톰', 'atom@email.com', '1450575459', 'USER'),
       ('커비', 'kirby@email.com', '1450575459', 'USER');

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
       ('11:00'),
       ('12:00');

INSERT INTO reservation (date, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE() - INTERVAL '9' DAY, 3, 12, 3, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '9' DAY, 2, 12, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '9' DAY, 1, 12, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '8' DAY, 3, 12, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '8' DAY, 2, 12, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '8' DAY, 1, 12, 2, 'RESERVED'),

       (CURRENT_DATE() - INTERVAL '7' DAY, 3, 10, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '7' DAY, 2, 9, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '7' DAY, 1, 8, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '6' DAY, 3, 7, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '6' DAY, 2, 6, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '6' DAY, 1, 5, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '5' DAY, 3, 5, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '5' DAY, 2, 4, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '5' DAY, 1, 4, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '4' DAY, 3, 3, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '4' DAY, 2, 3, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '4' DAY, 1, 3, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '3' DAY, 3, 2, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '3' DAY, 2, 2, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '3' DAY, 1, 2, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '2' DAY, 3, 2, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '2' DAY, 2, 1, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '2' DAY, 1, 1, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '1' DAY, 3, 1, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '1' DAY, 2, 1, 2, 'RESERVED'),
       (CURRENT_DATE() - INTERVAL '1' DAY, 1, 1, 2, 'RESERVED'),

       (CURRENT_DATE() + INTERVAL '1' DAY, 1, 1, 2, 'RESERVED'),
       (CURRENT_DATE() + INTERVAL '1' DAY, 2, 1, 2, 'RESERVED'),
       (CURRENT_DATE() + INTERVAL '2' DAY, 1, 1, 2, 'RESERVED'),

       (CURRENT_DATE() + INTERVAL '2' DAY, 1, 1, 3, 'WAITING'),
       (CURRENT_DATE() + INTERVAL '2' DAY, 1, 1, 4, 'WAITING'),
       (CURRENT_DATE() + INTERVAL '3' DAY, 1, 1, 2, 'PAYMENT_PENDING');

INSERT INTO payment (payment_key, order_id, amount, reservation_id)
VALUES ('paymentKey', 'orderId', 1000, 1),
       ('paymentKey', 'orderId', 1000, 30);
