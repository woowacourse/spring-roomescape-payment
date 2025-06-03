INSERT INTO USERS (name, role, email, password)
VALUES ('어드민', 'ADMIN', 'admin@email.com', 'password'),
       ('사용자1', 'USER', 'user1@email.com', 'password1'),
       ('사용자2', 'USER', 'user2wn@email.com', 'password2');

INSERT INTO TIME_SLOT (start_at)
VALUES ('10:00'),
       ('12:00'),
       ('14:00');

INSERT INTO THEME (name, description, thumbnail)
VALUES ('레벨1 탈출', '우테코 레벨1을 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨2 탈출', '우테코 레벨2를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨3 탈출', '우테코 레벨3을 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO PAYMENT (payment_key, order_id, order_name, amount)
VALUES ('payment_key_test_1', 'ROOM_ESCAPE_test_1', '방탈출 예약 결제 1건', 1000),
       ('payment_key_test_2', 'ROOM_ESCAPE_test_2', '방탈출 예약 결제 1건', 1000),
       ('payment_key_test_3', 'ROOM_ESCAPE_test_3', '방탈출 예약 결제 1건', 1100);


INSERT INTO RESERVATION (user_id, date, time_slot_id, theme_id, payment_id, reservation_type)
VALUES (2, CURRENT_DATE + 3, 1, 1, 1, 'RESERVED'),
       (2, CURRENT_DATE + 2, 1, 1, 2, 'RESERVED'),
       (2, CURRENT_DATE + 1, 1, 1, 3, 'RESERVED');

INSERT INTO RESERVATION (user_id, date, time_slot_id, theme_id, reservation_type)
VALUES (1, CURRENT_DATE + 3, 1, 1, 'WAITING'),
       (3, CURRENT_DATE + 3, 1, 1, 'WAITING');
