INSERT INTO theme(name, description, thumbnail) VALUES ('테마1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation_time(start_at) VALUES ('10:00');

INSERT INTO member(name, email, password, role) VALUES('어드민', 'admin@email.com', 'admin123', 'ADMIN');
INSERT INTO member(name, email, password, role) VALUES('리니', 'lini@email.com', 'lini123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('페드로', 'pedro@email.com', 'pedro123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('제이', 'junho@email.com', 'junho123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('미르', 'duho@email.com', 'duho123', 'GUEST');

INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id1', 'test_payment-key1', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id2', 'test_payment-key2', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id3', 'test_payment-key3', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id4', 'test_payment-key4', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id5', 'test_payment-key5', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id6', 'test_payment-key6', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id7', 'test_payment-key7', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id8', 'test_payment-key8', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id9', 'test_payment-key9', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id10', 'test_payment-key10', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id11', 'test_payment-key11', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id12', 'test_payment-key12', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id13', 'test_payment-key13', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id14', 'test_payment-key14', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id15', 'test_payment-key15', 1000);

INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 1, 1, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -7, CURRENT_DATE), 1, 2, 1, 2, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -6, CURRENT_DATE), 1, 3, 1, 3, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -5, CURRENT_DATE), 1, 3, 1, 4, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -5, CURRENT_DATE), 1, 3, 1, 5, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -7, CURRENT_DATE), 1, 3, 3, 6, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -7, CURRENT_DATE), 1, 3, 3, 7, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', -1, CURRENT_DATE), 1, 3, 2, 8, 'RESERVED');
-- 내일 날짜 2번 테마 1번 시간은 리니가 이미 예약한 상태임
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 1, 9,'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 2, 2, 10, 'RESERVED');
INSERT INTO reservation(VISIT_DATE, time_id, member_id, theme_id, payment_id, status) VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 3, 3, 11, 'RESERVED');

-- 내일 날짜 2번 테마 1번 시간에 예약 대기 순서대로 생성(페드로 -> 리니 -> 제이 -> 미르)
INSERT INTO waiting(member_id, theme_id, time_id, VISIT_DATE, payment_id, created_at) VALUES (3, 2, 1, DATEADD('DAY', 1, CURRENT_DATE), 12, DATEADD('HOUR', -4, CURRENT_TIMESTAMP));
INSERT INTO waiting(member_id, theme_id, time_id, VISIT_DATE, payment_id, created_at) VALUES (2, 2, 1, DATEADD('DAY', 1, CURRENT_DATE), 13, DATEADD('HOUR', -3,  CURRENT_TIMESTAMP));
INSERT INTO waiting(member_id, theme_id, time_id, VISIT_DATE, payment_id, created_at) VALUES (4, 2, 1, DATEADD('DAY', 1, CURRENT_DATE), 14, DATEADD('HOUR', -2,  CURRENT_TIMESTAMP));
INSERT INTO waiting(member_id, theme_id, time_id, VISIT_DATE, payment_id, created_at) VALUES (5, 2, 1, DATEADD('DAY', 1, CURRENT_DATE), 15, DATEADD('HOUR', -1,  CURRENT_TIMESTAMP));
