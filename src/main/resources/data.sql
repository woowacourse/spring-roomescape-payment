-- 테마 추가
INSERT INTO theme(name, description, thumbnail)
VALUES ('추리', '추리 테마입니다.', 'https://image.yes24.com/goods/73161943/L');
INSERT INTO theme(name, description, thumbnail)
VALUES ('아날로그식', '아날로그식 테마입니다.', 'https://image.yes24.com/goods/62087889/L');
INSERT INTO theme(name, description, thumbnail)
VALUES ('스테이지형', '스테이지형 테마입니다.', 'https://image.yes24.com/goods/125101417/L');
INSERT INTO theme(name, description, thumbnail)
VALUES ('협동형', '협동형 테마입니다.', 'https://image.yes24.com/momo/TopCate432/MidCate002/43115671.jpg');


-- 예약 시간 추가
INSERT INTO reservation_time(start_at)
VALUES ('10:00');
INSERT INTO reservation_time(start_at)
VALUES ('11:30');
INSERT INTO reservation_time(start_at)
VALUES ('13:00');
INSERT INTO reservation_time(start_at)
VALUES ('15:00');
INSERT INTO reservation_time(start_at)
VALUES ('17:00');
INSERT INTO reservation_time(start_at)
VALUES ('18:00');
INSERT INTO reservation_time(start_at)
VALUES ('19:00');
INSERT INTO reservation_time(start_at)
VALUES ('20:00');


-- 예약 추가
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', -2, CURRENT_DATE()), 1, 1);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', 16, CURRENT_DATE()), 1, 2);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('YEAR', 1, CURRENT_DATE()), 2, 1);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', 1, CURRENT_DATE()), 2, 2);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', -4, CURRENT_DATE()), 3, 3);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', 4, CURRENT_DATE()), 3, 4);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('MONTH', 1, CURRENT_DATE()), 3, 4);
INSERT INTO reservation_slot(date, reservation_time_id, theme_id)
VALUES (DATEADD('DAY', 4, CURRENT_DATE()), 3, 4);


-- 회원 추가
INSERT INTO member(name, email, password, role)
VALUES ('초코칩', 'dev.chocochip@gmail.com', '$2a$10$DORK.bYhWWXTEiWjwy9mxu.vodUPmuyiBeiShqRnSRcA1.buwN06K', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('이든', 'dev.eden@gmail.com', '$2a$10$Yf.YbwmgK4R8i9PAylmr5ubBdvl8ECjuHI.lYf3af6mEW3KC6b/3S', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('클로버', 'dev.clover@gmail.com', '$2a$10$Aj0C7gcvbSkKpro5bvGHVeOmsdbCRTZVm7bENr93g9EK2QdI40XlO', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('관리자', 'admin@roomescape.com', '$2a$10$pebanHuZMA0tfsogsWhgHOruvQQzIB/7N1MnN/.m8OSYO2fxlMtEC', 'ADMIN');
INSERT INTO member(name, email, password, role)
VALUES ('타칸', 'dev.tacan@gmail.com', '$2a$10$xFQbps5IJ6r9h69yhgYFe.hrbU59NA6snxaCVtllkcheE.nqq..UG', 'USER');


-- 결제 정보 추가
INSERT INTO payment(payment_key, order_id, order_name, method, total_amount, status, requested_at, approved_at)
VALUES ('payment_key', 'order_id', 'order_name', 'method', 1000, 'status', 'requested_at', 'approved_at');

-- 예약 목록 추가
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (1, 1, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (1, 3, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (1, 7, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (2, 2, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (2, 4, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (2, 8, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (3, 3, 1, DATEADD(HOUR, 1, CURRENT_TIME()), 'WAITING');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (3, 5, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (3, 6, 1, CURRENT_TIME(), 'BOOKED');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (2, 7, 1, DATEADD(HOUR, 1, CURRENT_TIME()), 'WAITING');
INSERT INTO reservation(member_id, reservation_slot_id, payment_id, created_at, status)
VALUES (3, 7, 1, DATEADD(HOUR, 2, CURRENT_TIME()), 'WAITING');
