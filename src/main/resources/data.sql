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

INSERT INTO payment (payment_key, amount, order_id)
VALUES ('payment_key1', 1000, 'order_id1'),
       ('payment_key2', 1000, 'order_id2'),
       ('payment_key3', 1000, 'order_id3'),
       ('payment_key4', 1000, 'order_id4'),
       ('payment_key5', 1000, 'order_id5'),
       ('payment_key6', 1000, 'order_id6'),
       ('payment_key7', 1000, 'order_id7'),
       ('payment_key8', 1000, 'order_id8'),
       ('payment_key9', 1000, 'order_id9');

INSERT INTO reservation (date, member_id, time_id, theme_id, status, create_at, payment_id)
VALUES ('2024-05-07', 1, 1, 1, 'BOOKED', '2024-05-07T11:44:30.000000', 1),
       ('2024-05-08', 1, 2, 1, 'BOOKED', '2024-05-08T11:44:30.000000', 2),
       ('2024-05-09', 1, 3, 1, 'BOOKED', '2024-05-09T11:44:30.000000', 3),
       ('2024-05-10', 1, 1, 2, 'BOOKED', '2024-05-10T11:44:30.000000', 4),
       ('2024-05-11', 1, 2, 2, 'BOOKED', '2024-05-11T11:44:30.000000', 5),
       ('2024-05-12', 1, 1, 3, 'BOOKED', '2024-05-12T11:44:30.000000', 6),
       ('2224-05-08', 2, 1, 1, 'BOOKED', '2024-05-07T11:44:30.000000', 7),
       ('2224-05-08', 3, 1, 1, 'STANDBY', '2024-05-08T11:44:30.000000', 8),
       ('2224-05-08', 1, 1, 1, 'STANDBY', '2024-05-09T11:44:30.000000', 9);

