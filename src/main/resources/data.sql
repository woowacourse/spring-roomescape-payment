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

INSERT INTO PAYMENT (order_id, payment_key, order_name, amount)
VALUES ('ROOM_ESCAPE_1', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_2', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_3', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_4', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_5', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_6', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_7', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_8', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_9', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_10', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_11', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_12', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_13', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_14', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_15', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_16', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000),
       ('ROOM_ESCAPE_17', 'example_payment_key', '테스트 방탈출 예약 결제 1건', 1000);

-- 현재 날짜로부터 3일 전 날짜를 동적으로 계산하여 삽입
INSERT INTO RESERVATION (user_id, date, time_slot_id, theme_id, payment_order_id)
VALUES (2, DATEADD(DAY, -3, CURRENT_DATE), 1, 3, 'ROOM_ESCAPE_1'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 5, 3, 'ROOM_ESCAPE_2'),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 5, 3, 'ROOM_ESCAPE_3'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 3, 'ROOM_ESCAPE_4'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 1, 3, 'ROOM_ESCAPE_5'),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 2, 2, 'ROOM_ESCAPE_6'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 1, 2, 'ROOM_ESCAPE_7'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 1, 2, 'ROOM_ESCAPE_8'),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 2, 2, 'ROOM_ESCAPE_9'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 1, 'ROOM_ESCAPE_10'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 4, 1, 'ROOM_ESCAPE_11'),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 5, 1, 'ROOM_ESCAPE_12'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 2, 4, 'ROOM_ESCAPE_13'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 3, 4, 'ROOM_ESCAPE_14'),
       (4, DATEADD(DAY, -3, CURRENT_DATE), 3, 5, 'ROOM_ESCAPE_15'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 4, 5, 'ROOM_ESCAPE_16'),
       (3, DATEADD(DAY, -3, CURRENT_DATE), 4, 6, 'ROOM_ESCAPE_17');
