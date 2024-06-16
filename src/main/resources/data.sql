INSERT INTO reservation_time (start_at)
VALUES ('15:40'),
       ('13:40'),
       ('17:40');

INSERT INTO member (name, email, password, role)
VALUES ('폴라(어드민)', 'polla@gmail.com', 'pollari99', 'ADMIN'),
       ('레모네(어드민)', 'lemone@wooteco.com', 'lemone1234', 'ADMIN'),
       ('산초(어드민)', 'sancho@wooteco.com', 'sancho', 'ADMIN'),
       ('제이(일반)', 'jay@wooteco.com', 'jay', 'MEMBER'),
       ('웨지(일반)', 'wedge@wooteco.com', 'wedge', 'MEMBER');

INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 - polla', '폴라 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마 - dobby', '도비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마 - pobi', '포비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id)
VALUES ('2024-04-30', 1, 1, 1),
       ('2024-05-01', 2, 2, 2);

INSERT INTO waiting (reservation_id, member_id, created_at)
VALUES (1, 2, '2024-05-27T13:45:30'),
       (2, 1, '2024-05-27T14:00:00'),
       (2, 3, '2024-05-28T12:12:12');

INSERT INTO payment (reservation_id, payment_key, type, order_id, order_name, status, requested_at, approved_at, total_amount, method, cancels)
VALUES (2, 'test paymentKey', 'test type', 'test orderId', 'test orderName', 'test status', 'test requestedAt', 'test approvedAt', 1000, 'test method', 'test cancels');
