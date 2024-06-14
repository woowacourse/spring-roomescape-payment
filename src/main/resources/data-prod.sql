INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 1', '설명 1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 2', '설명 2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 3', '설명 3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

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

INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (1, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (2, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (3, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (4, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (5, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (6, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (7, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (8, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (9, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (10, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (11, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
INSERT INTO payment (reservation_id, payment_key, order_name, requested_at, approved_at, currency, total_amount)
VALUES (12, 'data-payment-key', '방탈출 결제 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'KRW', 1000);
