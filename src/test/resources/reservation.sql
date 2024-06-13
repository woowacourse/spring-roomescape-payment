INSERT INTO member (id, email, password, name, role)
VALUES (1, 'alstn1@gmail.com', '1234', '구름a', 'USER'),
       (2, 'alstn2@gmail.com', '1234', '구름b', 'USER');

INSERT INTO theme (id, name, description, thumbnail)
VALUES (1, '고풍 한옥 마을', '한국의 전통적인 아름다움이 당신을 맞이합니다.', 'https://via.placeholder.com/150/92c952'),
       (2, '우주 탐험', '끝없는 우주에 숨겨진 비밀을 파헤치세요.', 'https://via.placeholder.com/150/771796'),
       (3, '시간여행', '과거와 미래를 오가며 역사의 비밀을 밝혀보세요.', 'https://via.placeholder.com/150/24f355'),
       (4, '마법의 숲', '요정과 마법사들이 사는 신비로운 숲 속으로!', 'https://via.placeholder.com/150/30f9e7');

INSERT INTO reservation_time (id, start_at)
VALUES (1, '09:00'),
       (2, '12:00'),
       (3, '17:00'),
       (4, '21:00');

INSERT INTO reservation (id, date, member_id, time_id, theme_id, status)
VALUES (1, '2024-04-05', 1, 1, 1, 'ACCEPTED'),
       (2, '2024-04-06', 2, 2, 1, 'ACCEPTED'),
       (3, '2024-04-06', 2, 3, 2, 'ACCEPTED'),
       (4, '2024-04-07', 1, 2, 2, 'ACCEPTED'),
       (5, '2024-04-07', 2, 2, 3, 'ACCEPTED'),
       (6, '2024-04-08', 1, 2, 3, 'ACCEPTED'),
       (7, '2024-04-08', 2, 3, 3, 'ACCEPTED'),
       (8, '2024-04-09', 1, 4, 4, 'ACCEPTED'),
       (9, '2024-04-10', 1, 1, 4, 'ACCEPTED'),
       (10, '2024-04-10', 2, 2, 4, 'ACCEPTED'),
       (11, '2024-04-11', 1, 2, 4, 'ACCEPTED');

INSERT INTO payment (id, payment_key, account_number, account_holder, bank_name, amount, reservation_id, pay_type)
VALUES (1, 'payment_key1', null, null, null, 10000, 1, 'TOSS_PAY'),
       (2, 'payment_key2', null, null, null, 10000, 2, 'TOSS_PAY'),
       (3, 'payment_key3', null, null, null, 10000, 3, 'TOSS_PAY'),
       (4, 'payment_key4', null, null, null, 10000, 4, 'TOSS_PAY'),
       (5, 'payment_key5', null, null, null, 10000, 5, 'TOSS_PAY'),
       (6, 'payment_key6', null, null, null, 10000, 6, 'TOSS_PAY'),
       (7, 'payment_key7', null, null, null, 10000, 7, 'TOSS_PAY'),
       (8, 'payment_key8', null, null, null, 10000, 8, 'TOSS_PAY'),
       (9, 'payment_key9', null, null, null, 10000, 9, 'TOSS_PAY'),
       (10, 'payment_key10', null, null, null, 10000, 10, 'TOSS_PAY'),
       (11, 'payment_key11', null, null, null, 10000, 11, 'TOSS_PAY');
