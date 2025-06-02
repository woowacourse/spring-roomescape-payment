INSERT INTO reservation_time(start_at)
VALUES ('10:00');
INSERT INTO reservation_time(start_at)
VALUES ('11:00');
INSERT INTO reservation_time(start_at)
VALUES ('12:00');
INSERT INTO reservation_time(start_at)
VALUES ('13:00');
INSERT INTO reservation_time(start_at)
VALUES ('14:00');

INSERT INTO theme(name, description, thumbnail)
VALUES ('테마1', '테마1입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마2', '테마2입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마3', '테마3입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마4', '테마4입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마5', '테마5입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마6', '테마6입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마7', '테마7입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마8', '테마8입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마9', '테마9입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마10', '테마10입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('테마11', '테마11입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO member (name, email, password, role)
VALUES ('사용자1', 'aaa@gmail.com', '1234', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('사용자2', 'bbb@gmail.com', '1234', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('사용자3', 'ccc@gmail.com', '1234', 'USER');
INSERT INTO member(name, email, password, role)
VALUES ('어드민', 'admin@gmail.com', '1234', 'ADMIN');


-- Payment 테이블 데이터 삽입 (payment_key는 'pay_001' ~ 'pay_014'로 구성)
INSERT INTO payment(payment_key, amount)
VALUES ('pay_001', 10000);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_002', 11000);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_003', 12000);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_004', 9000);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_005', 9500);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_006', 8000);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_007', 8500);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_008', 8700);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_009', 8900);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_010', 9100);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_011', 9300);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_012', 9700);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_013', 9900);
INSERT INTO payment(payment_key, amount)
VALUES ('pay_014', 10200);

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -1, CURRENT_DATE), 1, 1, 11, 'pay_001');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -1, CURRENT_DATE), 2, 2, 11, 'pay_002');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -1, CURRENT_DATE), 3, 3, 11, 'pay_003');

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -2, CURRENT_DATE), 1, 4, 9, 'pay_004');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -2, CURRENT_DATE), 2, 5, 9, 'pay_005');

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 1, 1, 1, 'pay_006');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 2, 2, 2, 'pay_007');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 3, 3, 3, 'pay_008');

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -4, CURRENT_DATE), 1, 4, 4, 'pay_009');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -4, CURRENT_DATE), 2, 5, 5, 'pay_010');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -4, CURRENT_DATE), 3, 1, 6, 'pay_011');

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -5, CURRENT_DATE), 1, 2, 7, 'pay_012');
INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -5, CURRENT_DATE), 2, 3, 8, 'pay_013');

INSERT INTO reservation(date, member_id, time_id, theme_id, payment_key)
VALUES (DATEADD('DAY', -8, CURRENT_DATE), 3, 4, 10, 'pay_014');

INSERT INTO waiting(member_id, reservation_id, created_at)
VALUES (2, 1, '2025-05-01 20:41:04.077864'); -- 1 번째 예약대기
INSERT INTO waiting(member_id, reservation_id, created_at)
VALUES (3, 1, '2025-05-02 20:41:04.077864'); -- 2 번째 예약대기
INSERT INTO waiting(member_id, reservation_id, created_at)
VALUES (1, 1, '2025-05-03 20:41:04.077864'); -- 3 번째 예약대기
INSERT INTO waiting(member_id, reservation_id, created_at)
VALUES (1, 2, '2025-05-21 20:41:04.077864'); -- 1 번째 예약 대기
INSERT INTO waiting(member_id, reservation_id, created_at)
VALUES (3, 3, '2025-05-21 20:41:04.077864'); -- 1 번째 예약 대기
