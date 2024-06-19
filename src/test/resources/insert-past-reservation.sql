INSERT INTO theme(name, description, thumbnail) VALUES ('테마1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail) VALUES ('테마3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation_time(start_at) VALUES ('10:00');

INSERT INTO member(name, email, password, role) VALUES('리니', 'lini@email.com', 'lini123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('릴리', 'lily@email.com', 'lily123', 'GUEST');
INSERT INTO member(name, email, password, role) VALUES('토미', 'tomi@email.com', 'tomi123', 'GUEST');

INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id1', 'test_payment-key1', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id2', 'test_payment-key2', 1000);
INSERT INTO payment(order_id, payment_key, amount) VALUES ('test_order_id3', 'test_payment-key3', 1000);


INSERT INTO reservation(visit_date, time_id, member_id, payment_id, theme_id) VALUES (DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 1, 1);
INSERT INTO reservation(visit_date, time_id, member_id, payment_id, theme_id) VALUES (DATEADD('DAY', -7, CURRENT_DATE), 1, 2, 2, 2);
INSERT INTO reservation(visit_date, time_id, member_id, payment_id, theme_id) VALUES (DATEADD('DAY', -8, CURRENT_DATE), 1, 3, 3, 3);
