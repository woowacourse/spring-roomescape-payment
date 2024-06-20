INSERT INTO member (name, email, password, role) VALUES ('냥인', 'nyangin@email.com', '1234', 'ADMIN');
INSERT INTO member (name, email, password, role) VALUES ('테니', 'tenny@email.com', '1234', 'MEMBER');
INSERT INTO member (name, email, password, role) VALUES ('미아', 'mia@email.com', '1234', 'MEMBER');

INSERT INTO reservation_time (start_at) VALUES ('13:00:00');
INSERT INTO reservation_time (start_at) VALUES ('14:00:00');

INSERT INTO theme (name, description, thumbnail) VALUES ('호러', '매우 무섭습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme (name, description, thumbnail) VALUES ('추리', '매우 어렵습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO payment (payment_key, order_id, amount, status) VALUES ('payment-key', 'order-id', 1000, 'CONFIRMED');

INSERT INTO reservation (member_id, date, time_id, theme_id, status, payment_id) VALUES (1, '2024-06-01', 1, 1, 'RESERVED', 1);
