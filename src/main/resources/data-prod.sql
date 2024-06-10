INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 1', '설명 1', 'url 1');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 2', '설명 2', 'url 2');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 3', '설명 3', 'url 3');

INSERT INTO reservation_time (start_at)
VALUES ('12:00');
INSERT INTO reservation_time (start_at)
VALUES ('13:00');
INSERT INTO reservation_time (start_at)
VALUES ('14:00');
INSERT INTO reservation_time (start_at)
VALUES ('15:00');

INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('admin', 'ADMIN', 'admin@email.com', 'password');
INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('user', 'USER', 'user@email.com', 'password');
INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('user2', 'USER', 'user2@email.com', 'password');

INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 1, 1, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 1, 1, 2, 'WAITING');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 1, 1, 3, 'WAITING');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 2, 1, 2, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 3, 1, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 4, 2, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 3, CURRENT_TIMESTAMP, 1, 3, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 2, CURRENT_TIMESTAMP, 1, 1, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 1, CURRENT_TIMESTAMP, 1, 1, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE - 1, CURRENT_TIMESTAMP,2, 1, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE + 1, CURRENT_TIMESTAMP, 1, 2, 1, 'BOOKED');
INSERT INTO reservation (date, created_at, time_id, theme_id, member_id, status)
VALUES (CURRENT_DATE + 2, CURRENT_TIMESTAMP, 1, 2, 1, 'BOOKED');

INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, amount, easy_pay_type, currency, created_at)
VALUES (1, 'PK_001', 'Payment for reservation 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5000.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(2, 'PK_002', 'Payment for reservation 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2500.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(3, 'PK_003', 'Payment for reservation 3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2500.00, 'KAKAO_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(4, 'PK_004', 'Payment for reservation 4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3000.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(5, 'PK_005', 'Payment for reservation 5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5000.00, 'KAKAO_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(6, 'PK_006', 'Payment for reservation 6', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 7000.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(7, 'PK_007', 'Payment for reservation 7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4500.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(8, 'PK_008', 'Payment for reservation 8', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5000.00, 'KAKAO_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(9, 'PK_009', 'Payment for reservation 9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5000.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(10, 'PK_010', 'Payment for reservation 10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5500.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(11, 'PK_011', 'Payment for reservation 11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6000.00, 'KAKAO_PAY', 'KRW', CURRENT_TIMESTAMP);
INSERT INTO payment (reservation_id,payment_key,order_name,requested_at,approved_at,amount,easy_pay_type,currency,created_at)
VALUES(12, 'PK_012', 'Payment for reservation 12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6500.00, 'TOSS_PAY', 'KRW', CURRENT_TIMESTAMP);
