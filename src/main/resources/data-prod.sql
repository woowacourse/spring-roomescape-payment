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
VALUES ('유저1', 'USER', 'user1@email.com', 'password');
INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('유저2', 'USER', 'user2@email.com', 'password');

INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 1, 1, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 2, 1, 2, '2024-04-02T11:20', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 1, 1, 3, '2024-04-02T12:30', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 3, 1, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 4, 2, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 3, 1, 3, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 2, 1, 1, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE - 1, 1, 1, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE, 1, 1, 1, '2024-04-02', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE + 1, 1, 2, 1, '2024-04-02T10:30', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE + 1, 1, 2, 2, '2024-04-02T10:40', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE + 2, 1, 2, 1, '2024-04-02T10:30', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE + 2, 1, 2, 2, '2024-04-02T11:30', 'paymentKeySample', 0L);
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_key, amount)
VALUES (CURRENT_DATE + 2, 1, 2, 3, '2024-04-02T12:30', 'paymentKeySample', 0L);
