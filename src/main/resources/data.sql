INSERT INTO member (name, email, password, role)
VALUES ('관리자', 'admin@gmail.com', '1234567890', 'ADMIN');
INSERT INTO member (name, email, password, role)
VALUES ('사용자', 'user@gmail.com', '1234567890', 'USER');

INSERT INTO theme (name, description, thumbnail)
VALUES ('레벨2', '설명이야', '썸네일이야');

INSERT INTO reservation_time (start_at)
VALUES ('10:00');

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id, status)
VALUES (DATEADD('DAY', + 1, CURRENT_DATE), 1, 1, 1, 'BOOKED');
INSERT INTO payment (payment_key, order_id, amount, order_name, status, reservation_id)
VALUES ('paymentKey', 'orderId', 1000, 'orderName', 'DONE', 1);
INSERT INTO reservation_waiting (reservation_id, member_id)
VALUES (1, 2);