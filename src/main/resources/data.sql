INSERT INTO USERS (name, role, email, password)
VALUES ('어드민', 'ADMIN', 'admin@email.com', 'password'),
       ('사용자', 'USER', 'user@email.com', 'userpass'),
       ('라젤', 'USER', 'razel@email.com', 'razelpass'),
       ('포포', 'USER', 'popo@email.com', 'popopass');

INSERT INTO TIME_SLOT (start_at)
VALUES ('10:00'),
       ('12:00'),
       ('14:00'),
       ('16:00'),
       ('18:00');

INSERT INTO THEME (name, description, thumbnail)
VALUES ('인기 테마 3순위 테마', '나는 1번째 테마지만, 인기 순위는 3위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 2순위 테마', '나는 2번째 테마지만, 인기 순위는 2위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 1순위 테마', '나는 3번째 테마지만, 인기 순위는 1위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 4순위 테마', '나는 4번째 테마지만, 인기 순위는 4위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 5순위 테마', '나는 5번째 테마지만, 인기 순위는 5위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('인기 테마 6순위 테마', '나는 6번째 테마지만, 인기 순위는 6위야',
        'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO PAYMENT (id, payment_key, order_id, order_name, amount)
VALUES 
    ( 1, 'payment_key_test_1', 'ROOM_ESCAPE_test_1', '방탈출 예약 결제 1건', 1000),
    ( 2, 'payment_key_test_2', 'ROOM_ESCAPE_test_2', '방탈출 예약 결제 1건', 1000),
    ( 3, 'payment_key_test_3', 'ROOM_ESCAPE_test_3', '방탈출 예약 결제 1건', 1100),
    ( 4, 'payment_key_test_4', 'ROOM_ESCAPE_test_4', '방탈출 예약 결제 1건', 1200),
    ( 5, 'payment_key_test_5', 'ROOM_ESCAPE_test_5', '방탈출 예약 결제 1건', 1300),
    ( 6, 'payment_key_test_6', 'ROOM_ESCAPE_test_6', '방탈출 예약 결제 1건', 1400),
    ( 7, 'payment_key_test_7', 'ROOM_ESCAPE_test_7', '방탈출 예약 결제 1건', 1500),
    ( 8, 'payment_key_test_8', 'ROOM_ESCAPE_test_8', '방탈출 예약 결제 1건', 1600),
    ( 9, 'payment_key_test_9', 'ROOM_ESCAPE_test_9', '방탈출 예약 결제 1건', 1700),
    ( 10, 'payment_key_test_10', 'ROOM_ESCAPE_test_10', '방탈출 예약 결제 1건', 1800),
    ( 11, 'payment_key_test_11', 'ROOM_ESCAPE_test_11', '방탈출 예약 결제 1건', 1900),
    ( 12, 'payment_key_test_12', 'ROOM_ESCAPE_test_12', '방탈출 예약 결제 1건', 2000);


INSERT INTO RESERVATION (user_id, date, time_slot_id, theme_id, payment_id, reservation_type)
VALUES
    (2, CURRENT_DATE - 5, 1, 3, 1, 'RESERVED'),
    (3, CURRENT_DATE - 5, 5, 3, 2, 'RESERVED'),
    (2, CURRENT_DATE - 4, 1, 3, 3, 'RESERVED'),
    (3, CURRENT_DATE - 4, 2, 2, 4, 'RESERVED'),
    (2, CURRENT_DATE - 3, 3, 3, 5, 'RESERVED'),
    (3, CURRENT_DATE - 3, 5, 2, 6, 'RESERVED'),
    (2, CURRENT_DATE - 2, 1, 2, 7, 'RESERVED'),
    (3, CURRENT_DATE - 2, 3, 1, 8, 'RESERVED'),
    (2, CURRENT_DATE - 1, 2, 4, 9, 'RESERVED'),
    (3, CURRENT_DATE - 1, 4, 5, 10, 'RESERVED'),
    (2, CURRENT_DATE, 3, 6, 11, 'RESERVED'),
    (3, CURRENT_DATE, 5, 6, 12, 'RESERVED');
