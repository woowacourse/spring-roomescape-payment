SET referential_integrity FALSE;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE waiting RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;

INSERT INTO theme (name, description, thumbnail)
VALUES ('이름1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름4', '설명4', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름5', '설명5', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름6', '설명6', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름7', '설명7', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름8', '설명8', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름9', '설명9', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름10', '설명10', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름11', '설명11', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름12', '설명12', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('이름13', '설명13', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg')
;
INSERT INTO reservation_time (start_at)
VALUES ('09:00'),
       ('10:00'),
       ('11:00'),
       ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00')
;
INSERT INTO reservation (member_id, date, time_id, theme_id, payment_key, order_id, amount, is_deleted)
VALUES (1, '2024-06-04', 1, 1, 'paymentKey1', 'orderId1', 1000, false),
       (1, '2024-06-04', 2, 1, 'paymentKey2', 'orderId2', 1000, false),
       (1, '2024-06-05', 3, 1, 'paymentKey3', 'orderId3', 1000, false),
       (2, '2024-06-05', 1, 2, 'paymentKey4', 'orderId4', 1000, false),
       (2, '2024-06-05', 1, 3, 'paymentKey5', 'orderId5', 1000, false),
       (2, '2024-06-09', 1, 2, 'paymentKey6', 'orderId6', 1000, false),
       (2, '2024-06-05', 1, 4, 'paymentKey7', 'orderId7', 1000, false),
       (3, '2024-06-06', 1, 2, 'paymentKey8', 'orderId8', 1000, false),
       (3, '2024-06-07', 1, 7, 'paymentKey9', 'orderId9', 1000, false),
       (3, '2024-06-08', 1, 8, 'paymentKey10', 'orderId10', 1000, false),
       (3, '2024-06-09', 1, 9, 'paymentKey11', 'orderId11', 1000, false),
       (3, '2024-06-10', 1, 10, 'paymentKey12', 'orderId12', 1000, false),
       (3, '2024-06-29', 2, 2, 'paymentKey13', 'orderId13', 1000, false),
       (1, '2024-06-30', 1, 1, 'paymentKey14', 'orderId14', 1000, false),
       (2, '2024-06-30', 2, 2, 'paymentKey15', 'orderId15', 1000, false),
       (2, '2024-06-30', 3, 2, 'paymentKey16', 'orderId16', 1000, false),
       (2, '2024-06-30', 4, 2, 'paymentKey17', 'orderId17', 1000, false),
       (2, '2024-06-30', 7, 2, 'paymentKey18', 'orderId18', 1000, false)
;
INSERT INTO waiting (member_id, reservation_id, is_deleted)
VALUES (3, 15, false),
       (1, 15, false),
       (1, 16, false)
;
