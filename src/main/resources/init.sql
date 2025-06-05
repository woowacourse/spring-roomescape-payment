INSERT INTO theme (name, description, thumbnail)
VALUES ('테마1', '재밌음', '/image/default.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마2', '무서움', '/image/default.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마3', '놀라움', '/image/default.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('10:00');
INSERT INTO reservation_time (start_at)
VALUES ('11:00');
INSERT INTO reservation_time (start_at)
VALUES ('12:00');

INSERT INTO member (name, email, password, role)
VALUES ('포라', 'forarium20@gmail.com', '1234', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('코기', 'ind07152@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('율무', 'ind07162@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('ADMIN', 'admin@naver.com', '1234', 'ADMIN');

INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (1, '2026-12-25', 1, 1);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (1, '2026-12-26', 1, 1);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (1, '2026-12-27', 1, 1);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (1, '2026-12-27', 2, 1);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (2, '2026-12-01', 1, 1);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (2, '2026-12-02', 1, 3);
INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (2, '2026-12-03', 1, 2);

INSERT INTO waiting (member_id, date, time_id, theme_id, created_at)
VALUES (1, '2026-12-01', 1, 1, CURRENT_TIMESTAMP);
INSERT INTO waiting (member_id, date, time_id, theme_id, created_at)
VALUES (2, '2026-12-25', 1, 1, CURRENT_TIMESTAMP);
INSERT INTO waiting (member_id, date, time_id, theme_id, created_at)
VALUES (2, '2026-12-26', 1, 1, CURRENT_TIMESTAMP);
INSERT INTO waiting (member_id, date, time_id, theme_id, created_at)
VALUES (3, '2026-12-25', 1, 1, CURRENT_TIMESTAMP);

INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('MC4wNDYzMzA0OTc2MDgy', 'paymentKey1', 1000, 'DONE', 1);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('SS4yMTYyNjMxNjY3MjAw', 'paymentKey2', 2000, 'DONE', 2);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('Mi4zNzE1NjE1NDEyMDA1', 'paymentKey3', 30000, 'DONE', 3);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('Ny4wOTk2Nzk0Nzc5MzI1', 'paymentKey4', 50000, 'DONE', 4);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('OC40NjM0NzI3MzUwNDM0', 'paymentKey5', 7000, 'DONE', 5);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('FS41MzI1NzM2NDk1ODk5', 'paymentKey6', 9000, 'DONE', 6);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('PC44MjY4MjA0NDY0NzI5', 'paymentKey7', 100000, 'DONE', 7);
INSERT INTO payment (order_id, payment_key, amount, status, reservation_id)
VALUES ('By42NzQzMTg2NzkzNTI3', 'paymentKey8', 500, 'CANCEL', null);
