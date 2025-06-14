INSERT INTO USERS (name, role, email, password)
VALUES
    ('어드민', 'ADMIN', 'admin@email.com', 'password'),
    ('포포', 'USER', 'popo@email.com', 'password'),
    ('브라운', 'USER', 'brown@email.com', 'password');

INSERT INTO RESERVATION_TIME (start_at)
VALUES
    ('10:00'),
    ('12:00'),
    ('14:00'),
    ('16:00'),
    ('18:00'),
    ('20:00');

INSERT INTO THEME (name, description, thumbnail)
VALUES
    ('레벨1 탈출', '우테코 레벨1을 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
    ('레벨2 탈출', '우테코 레벨2를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
    ('레벨3 탈출', '우테코 레벨3을 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
    ('레벨4 탈출', '우테코 레벨4를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
    ('레벨5 탈출', '우테코 레벨를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO RESERVATION (user_id, date, time_id, theme_id, status, payment_id, created_at)
VALUES
    (1, '2025-06-20', 1, 1, 'RESERVED', 1, '2025-05-19 21:00:00.000'),
    (1, '2025-06-20', 5, 2, 'RESERVED', 2, '2025-05-19 21:00:00.000'),
    (1, '2025-06-20', 6, 2, 'RESERVED', 3, '2025-05-19 21:00:00.000'),
    (2, '2025-06-21', 3, 3, 'RESERVED', 4, '2025-05-19 21:00:00.000'),
    (2, '2025-06-21', 1, 3, 'RESERVED', 5, '2025-05-19 21:00:00.000'),
    (2, '2025-06-20', 2, 3, 'RESERVED', 6, '2025-05-19 21:00:00.000'),
    (3, '2025-06-20', 1, 4, 'RESERVED', 7, '2025-05-19 21:00:00.000'),
    (3, '2025-06-20', 1, 5, 'RESERVED', 8, '2025-05-19 21:00:00.000'),
    (1, '2025-06-24', 5, 5, 'RESERVED', 9, '2025-05-19 21:10:00.000'),
    (2, '2025-06-24', 5, 5, 'WAITING', null, '2025-05-19 21:20:00.000'),
    (3, '2025-06-24', 5, 5, 'WAITING', null, '2025-05-19 21:30:00.000');

INSERT INTO PAYMENT (payment_key, order_id, total_amount, status)
VALUES
    ('paymentKey1', '100000', 1000, 'DONE'),
    ('paymentKey2', '200000', 2000, 'DONE'),
    ('paymentKey3', '300000', 2000, 'DONE'),
    ('paymentKey4', '400000', 3000, 'DONE'),
    ('paymentKey5', '500000', 3000, 'DONE'),
    ('paymentKey6', '600000', 3000, 'DONE'),
    ('paymentKey7', '700000', 4000, 'DONE'),
    ('paymentKey8', '800000', 5000, 'DONE'),
    ('paymentKey9', '900000', 5000, 'DONE');
