SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE reservation;
TRUNCATE TABLE member;
TRUNCATE TABLE reservation_slot;
TRUNCATE TABLE reservation_time;
TRUNCATE TABLE theme;
TRUNCATE TABLE payment;

ALTER TABLE reservation ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE member ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE reservation_slot ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE reservation_time ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE theme ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE payment ALTER COLUMN ID RESTART WITH 1;

-- 회원 추가
INSERT INTO member(name, email, password, role)
VALUES ('초코칩', 'dev.chocochip@gmail.com', '$2a$10$DORK.bYhWWXTEiWjwy9mxu.vodUPmuyiBeiShqRnSRcA1.buwN06K', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('관리자', 'admin@roomescape.com', '$2a$10$g6o5hc6cd2osCIArbIMKFOxnre8qIEDJ5bPzdabiHvcNBxQPEQoQ2', 'ADMIN');
INSERT INTO member(name, email, password, role)
VALUES ('타칸', 'dev.tacan@gmail.com', '$2a$10$xFQbps5IJ6r9h69yhgYFe.hrbU59NA6snxaCVtllkcheE.nqq..UG', 'USER');

-- 예약 시간 추가
INSERT INTO reservation_time(start_at)
VALUES ('12:00');
INSERT INTO reservation_time(start_at)
VALUES ('13:00');
INSERT INTO reservation_time(start_at)
VALUES ('14:00');

-- 테마 추가
INSERT INTO theme(name, description, thumbnail)
VALUES ('추리', '추리 테마입니다.', 'https://image.yes24.com/goods/73161943/L');
INSERT INTO theme(name, description, thumbnail)
VALUES ('아날로그식', '아날로그식 테마입니다.', 'https://image.yes24.com/goods/62087889/L');

-- 예약 슬롯 추가
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('MONTH', 1, CURRENT_DATE()), 3, 2);

-- 결제 정보 추가
INSERT INTO payment(payment_key, order_id, order_name, method, total_amount, status, requested_at, approved_at)
VALUES ('payment_key', 'order_id', 'order_name', 'method', 1000, 'status', 'requested_at', 'approved_at');

-- -- 예약 추가
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (1, 1, 1, DATEADD('HOUR', -2, CURRENT_TIME()), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (2, 1, 1, DATEADD('HOUR', -1, CURRENT_TIME()), 'WAITING');

SET REFERENTIAL_INTEGRITY TRUE;
