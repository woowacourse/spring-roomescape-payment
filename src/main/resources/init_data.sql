INSERT INTO users(role, name, email, password)
VALUES ('ROLE_MEMBER', 'name1', 'user1@email.com', '1234'),
       ('ROLE_MEMBER', 'name2', 'user2@email.com', '1234'),
       ('ROLE_MEMBER', 'name3', 'user3@email.com', '1234'),
       ('ROLE_MEMBER', 'name4', 'user4@email.com', '1234'),
       ('ROLE_MEMBER', 'name5', 'user5@email.com', '1234'),
       ('ROLE_MEMBER', 'name6', 'user6@email.com', '1234'),
       ('ROLE_MEMBER', 'name7', 'user7@email.com', '1234'),
       ('ROLE_MEMBER', 'name8', 'user8@email.com', '1234'),
       ('ROLE_MEMBER', 'name9', 'user9@email.com', '1234'),
       ('ROLE_MEMBER', 'name10', 'user10@email.com', '1234'),
       ('ROLE_ADMIN', '어드민', 'admin@email.com', '1234');

INSERT INTO reservation_time (start_at)
VALUES ('09:00'),
       ('10:00'),
       ('11:00'),
       ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00'),
       ('16:00'),
       ('17:00'),
       ( '18:00'),
       ( '19:00'),
       ( '20:00'),
       ( '21:00'),
       ( '22:00'),
       ( '23:00');

-- theme
INSERT INTO theme (name, description, thumbnail)
VALUES ('우테코 레벨1 탈출', '우테코 레벨1 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨2 탈출', '우테코 레벨2 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨3 탈출', '우테코 레벨3 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨4 탈출', '우테코 레벨4 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨5 탈출', '우테코 레벨5 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨6 탈출', '우테코 레벨6 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨7 탈출', '우테코 레벨7 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨8 탈출', '우테코 레벨8 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('우테코 레벨9 탈출', '우테코 레벨9 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ( '우테코 레벨10 탈출', '우테코 레벨10 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ( '우테코 레벨11 탈출', '우테코 레벨11 탈출 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

-- reservation
INSERT INTO reservation (date, time_id, theme_id, user_id, status)
VALUES
-- theme_id 1: 10건
(CURRENT_DATE - 3, 13, 1, 2, 'BOOKED'),
(CURRENT_DATE - 3, 12, 1, 3, 'BOOKED'),
(CURRENT_DATE - 3, 11, 1, 4, 'BOOKED'),
(CURRENT_DATE - 3, 10, 1, 5, 'BOOKED'),
(CURRENT_DATE - 3, 9, 1, 6, 'BOOKED'),
(CURRENT_DATE - 3, 8, 1, 7, 'BOOKED'),
(CURRENT_DATE - 3, 7, 1, 8, 'BOOKED'),
(CURRENT_DATE - 3, 6, 1, 9, 'BOOKED'),
(CURRENT_DATE - 3, 5, 1, 1, 'BOOKED'),
(CURRENT_DATE - 3, 4, 1, 1, 'BOOKED'),

-- theme_id 2: 9건
(CURRENT_DATE - 2, 11, 2, 2, 'BOOKED'),
(CURRENT_DATE - 2, 10, 2, 3, 'BOOKED'),
(CURRENT_DATE - 2, 9, 2, 4, 'BOOKED'),
(CURRENT_DATE - 2, 8, 2, 5, 'BOOKED'),
(CURRENT_DATE - 2, 7, 2, 6, 'BOOKED'),
(CURRENT_DATE - 2, 6, 2, 7, 'BOOKED'),
(CURRENT_DATE - 2, 5, 2, 8, 'BOOKED'),
(CURRENT_DATE - 2, 4, 2, 9, 'BOOKED'),
(CURRENT_DATE - 2, 3, 2, 1, 'BOOKED'),

-- theme_id 3: 8건
(CURRENT_DATE - 1, 9, 3, 2, 'BOOKED'),
(CURRENT_DATE - 1, 8, 3, 3, 'BOOKED'),
(CURRENT_DATE - 1, 7, 3, 4, 'BOOKED'),
(CURRENT_DATE - 1, 6, 3, 5, 'BOOKED'),
(CURRENT_DATE - 1, 5, 3, 6, 'BOOKED'),
(CURRENT_DATE - 1, 4, 3, 7, 'BOOKED'),
(CURRENT_DATE - 1, 3, 3, 8, 'BOOKED'),
(CURRENT_DATE - 1, 2, 3, 9, 'BOOKED'),

-- theme_id 4: 7건
(CURRENT_DATE - 7, 7, 4, 2, 'BOOKED'),
(CURRENT_DATE - 7, 6, 4, 3, 'BOOKED'),
(CURRENT_DATE - 7, 5, 4, 4, 'BOOKED'),
(CURRENT_DATE - 7, 4, 4, 5, 'BOOKED'),
(CURRENT_DATE - 7, 3, 4, 6, 'BOOKED'),
(CURRENT_DATE - 7, 2, 4, 7, 'BOOKED'),
(CURRENT_DATE - 7, 1, 4, 8, 'BOOKED'),

-- theme_id 5: 6건
(CURRENT_DATE - 6, 6, 5, 2, 'BOOKED'),
(CURRENT_DATE - 6, 5, 5, 3, 'BOOKED'),
(CURRENT_DATE - 6, 4, 5, 4, 'BOOKED'),
(CURRENT_DATE - 6, 3, 5, 5, 'BOOKED'),
(CURRENT_DATE - 6, 2, 5, 6, 'BOOKED'),
(CURRENT_DATE - 6, 1, 5, 7, 'BOOKED'),

-- theme_id 6: 5건
(CURRENT_DATE - 5, 5, 6, 2, 'BOOKED'),
(CURRENT_DATE - 5, 4, 6, 3, 'BOOKED'),
(CURRENT_DATE - 5, 3, 6, 4, 'BOOKED'),
(CURRENT_DATE - 5, 2, 6, 5, 'BOOKED'),
(CURRENT_DATE - 5, 1, 6, 6, 'BOOKED'),

-- theme_id 7: 4건
(CURRENT_DATE - 4, 4, 7, 2, 'BOOKED'),
(CURRENT_DATE - 4, 3, 7, 3, 'BOOKED'),
(CURRENT_DATE - 4, 2, 7, 4, 'BOOKED'),
(CURRENT_DATE - 4, 1, 7, 5, 'BOOKED'),

-- theme_id 8: 3건
(CURRENT_DATE - 3, 3, 8, 2, 'BOOKED'),
(CURRENT_DATE - 3, 2, 8, 3, 'BOOKED'),
(CURRENT_DATE - 3, 1, 8, 4, 'BOOKED'),

-- theme_id 9: 2건
(CURRENT_DATE - 2, 2, 9, 2, 'BOOKED'),
(CURRENT_DATE - 2, 1, 9, 3, 'BOOKED'),

-- theme_id 10: 1건
(CURRENT_DATE - 1, 1, 10, 2, 'BOOKED');


-- theme_id 1: 예약 10건, waiting 2명씩
INSERT INTO waiting (date, member_id, theme_id, time_id)
VALUES
-- (date, user_id, theme_id, time_id)
(CURRENT_DATE - 3, 5, 1, 13),
(CURRENT_DATE - 3, 6, 1, 13),

(CURRENT_DATE - 3, 7, 1, 12),
(CURRENT_DATE - 3, 8, 1, 12),

(CURRENT_DATE - 3, 9, 1, 11),
(CURRENT_DATE - 3, 10, 1, 11),

(CURRENT_DATE - 3, 11, 1, 10),
(CURRENT_DATE - 3, 5, 1, 10),

(CURRENT_DATE - 3, 6, 1, 9),
(CURRENT_DATE - 3, 7, 1, 9),

(CURRENT_DATE - 3, 8, 1, 8),
(CURRENT_DATE - 3, 9, 1, 8),

(CURRENT_DATE - 3, 10, 1, 7),
(CURRENT_DATE - 3, 11, 1, 7),

(CURRENT_DATE - 3, 5, 1, 6),
(CURRENT_DATE - 3, 6, 1, 6),

(CURRENT_DATE - 3, 7, 1, 5),
(CURRENT_DATE - 3, 8, 1, 5),

(CURRENT_DATE - 3, 9, 1, 4),
(CURRENT_DATE - 3, 10, 1, 4);


INSERT INTO waiting (date, member_id, theme_id, time_id)
VALUES
    (CURRENT_DATE - 3, 1, 1, 13),
    (CURRENT_DATE - 3, 2, 1, 13),

    (CURRENT_DATE - 3, 3, 1, 12),
    (CURRENT_DATE - 3, 4, 1, 12),

    (CURRENT_DATE - 3, 5, 1, 11),
    (CURRENT_DATE - 3, 6, 1, 11),

    (CURRENT_DATE - 3, 7, 1, 10),
    (CURRENT_DATE - 3, 8, 1, 10),

    (CURRENT_DATE - 3, 9, 1, 9),
    (CURRENT_DATE - 3, 10, 1, 9),

    (CURRENT_DATE - 3, 11, 1, 8),
    (CURRENT_DATE - 3, 1, 1, 8),

    (CURRENT_DATE - 3, 2, 1, 7),
    (CURRENT_DATE - 3, 3, 1, 7),

    (CURRENT_DATE - 3, 4, 1, 6),
    (CURRENT_DATE - 3, 5, 1, 6),

    (CURRENT_DATE - 3, 6, 1, 5),
    (CURRENT_DATE - 3, 7, 1, 5),

    (CURRENT_DATE - 3, 8, 1, 4),
    (CURRENT_DATE - 3, 9, 1, 4);


INSERT INTO waiting (date, member_id, theme_id, time_id)
VALUES
    (CURRENT_DATE - 2, 10, 2, 11),
    (CURRENT_DATE - 2, 11, 2, 11),

    (CURRENT_DATE - 2, 1, 2, 10),
    (CURRENT_DATE - 2, 2, 2, 10),

    (CURRENT_DATE - 2, 3, 2, 9),
    (CURRENT_DATE - 2, 4, 2, 9),

    (CURRENT_DATE - 2, 5, 2, 8),
    (CURRENT_DATE - 2, 6, 2, 8),

    (CURRENT_DATE - 2, 7, 2, 7),
    (CURRENT_DATE - 2, 8, 2, 7),

    (CURRENT_DATE - 2, 9, 2, 6),
    (CURRENT_DATE - 2, 10, 2, 6),

    (CURRENT_DATE - 2, 11, 2, 5),
    (CURRENT_DATE - 2, 1, 2, 5),

    (CURRENT_DATE - 2, 2, 2, 4),
    (CURRENT_DATE - 2, 3, 2, 4),

    (CURRENT_DATE - 2, 4, 2, 3),
    (CURRENT_DATE - 2, 5, 2, 3);


INSERT INTO waiting (date, member_id, theme_id, time_id)
VALUES
    (CURRENT_DATE - 1, 6, 3, 9),
    (CURRENT_DATE - 1, 7, 3, 9),

    (CURRENT_DATE - 1, 8, 3, 8),
    (CURRENT_DATE - 1, 9, 3, 8),

    (CURRENT_DATE - 1, 10, 3, 7),
    (CURRENT_DATE - 1, 11, 3, 7),

    (CURRENT_DATE - 1, 1, 3, 6),
    (CURRENT_DATE - 1, 2, 3, 6),

    (CURRENT_DATE - 1, 3, 3, 5),
    (CURRENT_DATE - 1, 4, 3, 5),

    (CURRENT_DATE - 1, 5, 3, 4),
    (CURRENT_DATE - 1, 6, 3, 4),

    (CURRENT_DATE - 1, 7, 3, 3),
    (CURRENT_DATE - 1, 8, 3, 3),

    (CURRENT_DATE - 1, 9, 3, 2),
    (CURRENT_DATE - 1, 10, 3, 2);

-- payment 예시: reservation_id = 1에 대한 결제
INSERT INTO payment(payment_key, order_id, amount, reservation_id, status)
VALUES ('payment-key-001', 'order-001', 1000, 1, 'COMPLETED');

-- reservation_id = 2에 대한 결제
INSERT INTO payment(payment_key, order_id, amount, reservation_id, status)
VALUES ('payment-key-002', 'order-002', 1000, 2, 'COMPLETED');
