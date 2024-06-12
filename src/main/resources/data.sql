INSERT INTO theme (name, description, thumbnail, price)
VALUES ('이름1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 1000),
       ('이름2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 2000),
       ('이름3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 3000),
       ('이름4', '설명4', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 4000),
       ('이름5', '설명5', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 5000),
       ('이름6', '설명6', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 10000),
       ('이름7', '설명7', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 9000),
       ('이름8', '설명8', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 5000),
       ('이름9', '설명9', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 1000),
       ('이름10', '설명10', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 20000),
       ('이름11', '설명11', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 7000),
       ('이름12', '설명12', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 1500),
       ('이름13', '설명13', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 20000);

INSERT INTO reservation_time (start_at)
VALUES ('09:00'),
       ('10:00'),
       ('11:00'),
       ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00');

INSERT INTO member (name, email, password, role)
VALUES ('aaa', '111@aaa.com', 'asd', 'USER'),
       ('teco', 'admin@gmail.com', 'asd', 'ADMIN'),
       ('potato', 'user@gmail.com', 'asd', 'USER'),
       ('potato1', 'user2@gmail.com', 'asd', 'USER'),
       ('potato2', 'user3@gmail.com', 'asd', 'USER');

INSERT INTO reservation(date, time_id, theme_id, member_id)
VALUES (TIMESTAMPADD(DAY, -1, CURRENT_DATE), 1, 1, 1),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 2, 1, 2),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 3, 1, 3),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 2, 1),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 2, 3, 2),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 3, 4, 3),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 5, 1),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 2, 6, 2),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 3, 7, 3),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 8, 1),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 9, 2),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 10, 3),
       (TIMESTAMPADD(WEEK, -1, CURRENT_DATE), 1, 11, 1),
       (TIMESTAMPADD(DAY, -3, CURRENT_DATE), 1, 2, 1),
       (TIMESTAMPADD(DAY, -2, CURRENT_DATE), 1, 7, 1),
       (TIMESTAMPADD(DAY, -2, CURRENT_DATE), 1, 2, 1),
       (TIMESTAMPADD(DAY, 2, CURRENT_DATE), 1, 1, 1),
       (TIMESTAMPADD(DAY, 1, CURRENT_DATE), 1, 1, 3),
       (TIMESTAMPADD(DAY, 3, CURRENT_DATE), 1, 1, 1);

INSERT INTO reservation_waiting(date, time_id, theme_id, member_id, denied_at)
VALUES (TIMESTAMPADD(DAY, 2, CURRENT_DATE), 1, 1, 3, NULL),
       (TIMESTAMPADD(DAY, 2, CURRENT_DATE), 1, 1, 1, NULL),
       (TIMESTAMPADD(DAY, 2, CURRENT_DATE), 1, 1, 4, NULL),
       (TIMESTAMPADD(DAY, 2, CURRENT_DATE), 1, 1, 2, NULL);

INSERT INTO payment(reservation_id, payment_key, order_id, total_amount)
VALUES (2, 'dJv2eBNjG0Pox', 'orderId', 1000),
       (5, '2c0lhlsxtMlGK', 'ooooorrrd', 2000);
