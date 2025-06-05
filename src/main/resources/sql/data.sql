INSERT INTO reservation_time (start_at)
VALUES ('10:00');
INSERT INTO reservation_time (start_at)
VALUES ('13:00');
INSERT INTO reservation_time (start_at)
VALUES ('15:00');
INSERT INTO reservation_time (start_at)
VALUES ('17:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 A', '테마 A입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 B', '테마 B입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 C', '테마 C입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 D', '테마 D입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 E', '테마 E입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO member (name, email, password, role)
VALUES ('Danny', 'danny@example.com', '0000', 'ADMIN');
INSERT INTO member (name, email, password, role)
VALUES ('Sooyang', 'sooyang@example.com', '1234', 'USER');

INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-22', 2, 4, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-23', 2, 3, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-24', 2, 3, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-25', 1, 3, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-26', 2, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-26', 2, 3, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-27', 1, 3, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-27', 2, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-28', 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-28', 2, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-04-30', 2, 5, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-09', 2, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-10', 2, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-13', 2, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-26', 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-27', 2, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES ('2025-05-25', 2, 5, 1);

INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES ('2026-05-25', 2, 5, 1);
INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES ('2026-05-26', 2, 5, 1);
INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES ('2026-05-27', 2, 5, 1);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1001', 'PAYKEY-1001', 50000, 1);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1002', 'PAYKEY-1002', 50000, 2);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1003', 'PAYKEY-1003', 60000, 3);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1004', 'PAYKEY-1004', 55000, 4);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1005', 'PAYKEY-1005', 55000, 5);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1006', 'PAYKEY-1006', 60000, 6);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1007', 'PAYKEY-1007', 50000, 7);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1008', 'PAYKEY-1008', 50000, 8);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1009', 'PAYKEY-1009', 55000, 9);

INSERT INTO payment (order_id, payment_key, amount, reservation_id)
VALUES ('ORD-1010', 'PAYKEY-1010', 55000, 10);
