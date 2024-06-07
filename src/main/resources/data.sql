INSERT INTO theme(name, description, thumbnail) VALUES ('테마1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation_time(start_at) VALUES ('10:00');

INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');
INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');
INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');
INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');
INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');
INSERT INTO payment(amount, approved_at, requested_at, order_id, payment_key) VALUES (1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'orderId', 'paymentKey');

INSERT INTO member(name, email, password, role) VALUES('어드민', 'admin@email.com', 'admin123', 'ADMIN');
INSERT INTO member(name, email, password, role) VALUES('리니', 'lini@email.com', 'lini123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('피케이', 'pk@email.com', 'pkpk123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('제이', 'junho@email.com', 'junho123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('미르', 'duho@email.com', 'duho123', 'GUEST');

INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -1, CURRENT_DATE),1, 1);
INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -1, CURRENT_DATE),1, 2);
INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -7, CURRENT_DATE),1, 1);
INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -7, CURRENT_DATE),1, 3);
INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -5, CURRENT_DATE),1, 1);
INSERT INTO reservation_detail(date, time_id, theme_id) VALUES (DATEADD('DAY', -6, CURRENT_DATE),1, 1);

INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (1, 1, 1, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (3, 2, 2, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (5, 3, 3, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (4, 3, 4, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (2, 3, 5, 'RESERVED', DATEADD('DAY', -2, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, payment_id, status, created_at) VALUES (6, 4, 6, 'RESERVED', DATEADD('DAY', -1, CURRENT_DATE));
INSERT INTO reservation(detail_id, member_id, status, created_at) VALUES (2, 4, 'WAITING', DATEADD('DAY', -1, CURRENT_DATE));

