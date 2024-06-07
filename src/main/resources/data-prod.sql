INSERT INTO theme (name, description, thumbnail) VALUES
('테마 1', '설명 1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('테마 2', '설명 2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('테마 3', '설명 3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation_time (start_at) VALUES
                                    ('12:00'),
                                    ('13:00'),
                                    ('14:00'),
                                    ('15:00');

INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD) VALUES
            ('admin', 'ADMIN', 'admin@email.com', 'password'),
            ('유저1', 'USER', 'user1@email.com', 'password'),
            ('유저2', 'USER', 'user2@email.com', 'password');

INSERT INTO payment (payment_key, order_id, amount) VALUES
                ('paymentKeySample', 'orderIdSample', 10L),
                ('paymentKeySample', 'orderIdSample', 20L),
                ('paymentKeySample', 'orderIdSample', 30L),
                ('paymentKeySample', 'orderIdSample', 40L),
                ('paymentKeySample', 'orderIdSample', 50L),
                ('paymentKeySample', 'orderIdSample', 60L),
                ('paymentKeySample', 'orderIdSample', 70L),
                ('paymentKeySample', 'orderIdSample', 80L),
                ('paymentKeySample', 'orderIdSample', 90L),
                ('paymentKeySample', 'orderIdSample', 100L),
                ('paymentKeySample', 'orderIdSample', 110L),
                ('paymentKeySample', 'orderIdSample', 120L),
                ('paymentKeySample', 'orderIdSample', 130L),
                ('paymentKeySample', 'orderIdSample', 140L);

INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, payment_id) VALUES
                                    (CURRENT_DATE - 3, 1, 1, 1, '2024-04-02', 1),
                                    (CURRENT_DATE - 3, 2, 1, 2, '2024-04-02T11:20', 2),
                                    (CURRENT_DATE - 3, 1, 1, 3, '2024-04-02T12:30', 3),
                                    (CURRENT_DATE - 3, 3, 1, 1, '2024-04-02', 4),
                                    (CURRENT_DATE - 3, 4, 2, 1, '2024-04-02', 5),
                                    (CURRENT_DATE - 3, 1, 3, 1, '2024-04-02', 6),
                                    (CURRENT_DATE - 2, 1, 1, 1, '2024-04-02', 7),
                                    (CURRENT_DATE - 1, 1, 1, 1, '2024-04-02', 8),
                                    (CURRENT_DATE, 1, 1, 1, '2024-04-02', 9),
                                    (CURRENT_DATE + 1, 1, 2, 1, '2024-04-02T10:30', 10),
                                    (CURRENT_DATE + 1, 1, 2, 2, '2024-04-02T10:40', 11),
                                    (CURRENT_DATE + 2, 1, 2, 1, '2024-04-02T10:30', 12),
                                    (CURRENT_DATE + 2, 1, 2, 2, '2024-04-02T11:30', 13),
                                    (CURRENT_DATE + 2, 1, 2, 3, '2024-04-02T12:30', 14);
