INSERT INTO member (name, email, password)
VALUES ('클로버', 'clover@gmail.com', 'password');
INSERT INTO member (name, email, password)
VALUES ('페드로', 'pedro@gmail.com', 'password');
INSERT INTO member (name, email, password, role)
VALUES ('관리자', 'admin@gmail.com', 'password', 'ADMIN');

INSERT INTO reservation_time (start_at)
VALUES ('10:00:00');
INSERT INTO reservation_time (start_at)
VALUES ('12:00:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('공포', '완전 무서운 테마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('힐링', '완전 힐링되는 테마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('몽환', '완전 몽환적인 테마', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation (date, time_id, theme_id)
VALUES ('2099-12-31', 1, 1);
INSERT INTO reservation (date, time_id, theme_id)
VALUES ('2099-12-31', 1, 2);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -3, NOW()), 'yyyy-MM-dd'), 1, 1);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -3, NOW()), 'yyyy-MM-dd'), 1, 2);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -2, NOW()), 'yyyy-MM-dd'), 1, 1);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -4, NOW()), 'yyyy-MM-dd'), 1, 2);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -2, NOW()), 'yyyy-MM-dd'), 1, 2);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', -2, NOW()), 'yyyy-MM-dd'), 1, 3);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', 2, NOW()), 'yyyy-MM-dd'), 1, 3);
INSERT INTO reservation (date, time_id, theme_id)
VALUES (FORMATDATETIME(DATEADD('DAY', 3, NOW()), 'yyyy-MM-dd'), 1, 1);

INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (2, 1, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (1, 2, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (2, 3, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (1, 4, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (2, 5, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (1, 6, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (1, 7, CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, created_at)
VALUES (1, 8, CURRENT_TIMESTAMP);

INSERT INTO member_reservation (member_id, reservation_id, status, created_at)
VALUES (1, 3, 'CANCELLATION_WAITING', CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, status, created_at)
VALUES (3, 3, 'CANCELLATION_WAITING', CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, status, created_at)
VALUES (1, 9, 'PAYMENT_REQUIRED', CURRENT_TIMESTAMP);
INSERT INTO member_reservation (member_id, reservation_id, status, created_at)
VALUES (1, 10, 'PAYMENT_REQUIRED', CURRENT_TIMESTAMP);

INSERT INTO payment (payment_key, order_id, amount, created_at, member_reservation_id)
VALUES ('tgen_20240605163415F3v93', '1717572855093MC45Nzk0MjM1MzkyODc1', 21000, DATEADD('DAY', -2, NOW()), 4);
