INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'test@email.com', 'password', 'ADMIN'),
       ('유저', 'user@email.com', 'password', 'USER'),
       ('릴리', 'lily@email.com', 'password', 'USER');

INSERT INTO reservation_time (start_at)
VALUES ('13:00'),
       ('14:00'),
       ('15:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('테마1', '테마1 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마2', '테마2 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마3', '테마3 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마4', '테마4 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마5', '테마5 설명', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation (date, member_id, time_id, theme_id, status, create_at)
VALUES ('2024-05-07', 1, 1, 1, 'BOOKED', '2024-05-07T11:44:30.000000'),
       ('2224-05-08', 2, 1, 1, 'BOOKED', '2024-05-07T11:44:30.000000'),
       ('2224-05-08', 3, 1, 1, 'STANDBY', '2024-05-08T11:44:30.000000'),
       ('2224-05-08', 1, 1, 1, 'STANDBY', '2024-05-09T11:44:30.000000'),
       ('2224-05-09', 2, 1, 1, 'BOOKED', '2024-05-09T11:44:30.000000');

INSERT INTO payment (reservation_id, payment_key, amount, order_id)
VALUES (1, 'payment_key1', 1000, 'order_id1'),
       (2, 'payment_key2', 1000, 'order_id2'),
       (3, 'payment_key3', 1000, 'order_id3'),
       (4, 'payment_key4', 1000, 'order_id4');
