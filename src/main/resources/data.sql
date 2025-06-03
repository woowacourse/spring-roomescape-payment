DELETE
FROM reservation;
DELETE
FROM reservation_time;
DELETE
FROM theme;
DELETE
FROM member;
DELETE
FROM payment;

-- member 데이터
INSERT INTO member(name, email, password, role)
VALUES ('플린트', 'test@test.com', 'test', 'USER'),
       ('훌라', 'test2@test2.com', 'test2', 'USER'),
       ('어드민', 'admin@admin.com', 'admin', 'ADMIN');

-- theme 데이터
INSERT INTO theme (name, description, thumbnail)
VALUES ('공포의 방', '소름 끼치는 공포 테마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('추리의 방', '논리력으로 푸는 추리 테마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('시간 여행자', '과거와 미래를 넘나드는 방 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

-- reservation_time 데이터
INSERT INTO reservation_time (start_at)
VALUES ('10:00'),
       ('12:00'),
       ('14:00'),
       ('16:00');

-- reservation 데이터
INSERT INTO reservation(member_id, date, time_id, theme_id, status, create_at)
VALUES (1, DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE)),
       (2, DATEADD('DAY', -2, CURRENT_DATE), 2, 2, 'RESERVED', DATEADD('DAY', -2, CURRENT_DATE)),
       (3, DATEADD('DAY', -3, CURRENT_DATE), 3, 2, 'RESERVED', DATEADD('DAY', -3, CURRENT_DATE)),
       (1, DATEADD('DAY', -1, CURRENT_DATE), 2, 3, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE)),
       (2, DATEADD('DAY', -4, CURRENT_DATE), 3, 3, 'RESERVED', DATEADD('DAY', -4, CURRENT_DATE)),
       (3, DATEADD('DAY', -5, CURRENT_DATE), 4, 3, 'RESERVED', DATEADD('DAY', -5, CURRENT_DATE)),
       (1, '2025-07-01', 1, 1, 'RESERVED', '2025-05-20 10:15:00'),
       (2, '2025-07-01', 1, 1, 'WAIT', '2025-05-21 08:00:00'),
       (3, '2025-07-01', 1, 1, 'WAIT', '2025-05-21 08:01:00');

INSERT INTO payment(order_id, payment_key, amount, reservation_id)
VALUES ('orderId1', 'paymentKey1', 1000, 1),
        ('orderId2', 'paymentKey2', 1000, 2),
        ('orderId3', 'paymentKey3', 1000, 3),
        ('orderId4', 'paymentKey4', 1000, 4),
        ('orderId5', 'paymentKey5', 1000, 5),
        ('orderId6', 'paymentKey6', 1000, 6),
        ('orderId7', 'paymentKey7', 1000, 7);
