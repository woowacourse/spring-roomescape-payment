INSERT INTO time_slot(start_at)
VALUES ('00:00'),
       ('11:00'),
       ('12:00'),
       ('13:00');

INSERT INTO member(name, email, password, role)
VALUES ('어드민', 'test@email.com', '1234', 'ADMIN'),
       ('사용자', 'test2@email.com', '1234', 'USER');

INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨1 탈출', '우테코 레벨2를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨2 탈출', '우테코 레벨3를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨3 탈출', '우테코 레벨4를 탈출하는 내용입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO payment(payment_key, order_id, amount)
VALUES ('payment_key1', 'order_id1', 1000),
       ('payment_key2', 'order_id2', 2000),
       ('payment_key3', 'order_id3', 3000);

INSERT INTO reservation(member_id, date, time_id, theme_id, payment_id, status)
VALUES (1, '2024-05-31', 1, 1, 1,'BOOKING'),
       (1, '2024-06-02', 1, 1, 1,'BOOKING'),
       (1, '2024-05-02', 1, 1, 1,'BOOKING'),
       (2, '2024-06-02', 3, 3, 1,'BOOKING'),
       (2, '2024-06-01', 3, 3, 1,'BOOKING'),
       (2, '2024-05-31', 3, 3, 1,'BOOKING'),
       (2, '2099-04-30', 1, 1, 1,'BOOKING'),
       (1, DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 1, 'BOOKING'),
       (1, DATEADD('DAY', -2, CURRENT_DATE), 1, 1, 1, 'BOOKING'),
       (1, DATEADD('DAY', -15, CURRENT_DATE), 1, 1, 1, 'BOOKING'),
       (1, DATEADD('DAY', -2, CURRENT_DATE), 1, 3, 1, 'BOOKING'),
       (1, DATEADD('DAY', -3, CURRENT_DATE), 1, 3, 1, 'BOOKING'),
       (1, DATEADD('DAY', -4, CURRENT_DATE), 1, 3, 1, 'BOOKING');

INSERT INTO waiting(member_id, date, time_id, theme_id, status)
VALUES (2, '2024-05-31', 1, 1, 'WAITING'),
       (2, '2024-06-02', 1, 1, 'WAITING'),
       (2, '2024-05-02', 1, 1, 'WAITING'),
       (1, '2024-06-02', 3, 3, 'WAITING'),
       (1, '2024-06-01', 3, 3, 'WAITING'),
       (1, '2024-05-31', 3, 3, 'WAITING'),
       (1, '2099-04-29', 1, 1, 'WAITING');

