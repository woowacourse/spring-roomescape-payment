INSERT INTO reservation_time(start_at)
VALUES ('10:00');
INSERT INTO reservation_time(start_at)
VALUES ('11:00');

INSERT INTO theme(name, description, thumbnail)
VALUES ('이름1', '설명1', '썸네일1');
INSERT INTO theme(name, description, thumbnail)
VALUES ('이름2', '설명2', '썸네일2');

INSERT INTO member(name, email, password, role)
VALUES ('운영진', 'admin@naver.com', '1234', 'ADMIN');
INSERT INTO member(name, email, password, role)
VALUES ('홍길동', 'member@naver.com', '1234', 'MEMBER');

INSERT INTO reservation_status(status, rank)
VALUES ('BOOKED', null);
INSERT INTO reservation_status(status, rank)
VALUES ('WAITING', 1);
INSERT INTO reservation_status(status, rank)
VALUES ('WAITING', 2);

INSERT INTO payment(order_id, payment_key, amount)
VALUES ('ORD-123456A', 'tgen_20250511A01', 1000);
INSERT INTO payment(order_id, payment_key, amount)
VALUES ('ORD-123456B', 'tgen_20250511B01', 2000);
INSERT INTO payment(order_id, payment_key, amount)
VALUES ('ORD-123456C', 'tgen_20250511C01', 3000);

INSERT INTO reservation(date, time_id, theme_id, member_id, status_id, payment_id)
VALUES ('2025-06-11', 1, 1, 1, 1, 1);
INSERT INTO reservation(date, time_id, theme_id, member_id, status_id, payment_id)
VALUES ('2025-06-11', 1, 1, 1, 2, 2);
INSERT INTO reservation(date, time_id, theme_id, member_id, status_id, payment_id)
VALUES ('2025-06-11', 1, 1, 1, 3, 3);
