INSERT INTO theme (name, description, thumbnail)
VALUES ('고풍 한옥 마을', '한국의 전통적인 아름다움이 당신을 맞이합니다.', 'https://via.placeholder.com/150/92c952'),
       ('우주 탐험', '끝없는 우주에 숨겨진 비밀을 파헤치세요.', 'https://via.placeholder.com/150/771796'),
       ('시간여행', '과거와 미래를 오가며 역사의 비밀을 밝혀보세요.', 'https://via.placeholder.com/150/24f355'),
       ('마법의 숲', '요정과 마법사들이 사는 신비로운 숲 속으로!', 'https://via.placeholder.com/150/30f9e7'),
       ('타임캡슐', '오래된 타임캡슐을 찾아내어 그 안의 비밀을 풀어보세요.', 'https://via.placeholder.com/150/56a8c2'),
       ('로맨틱 유럽 여행', '로맨틱한 분위기 속에서 유럽을 여행하세요.', 'https://via.placeholder.com/150/7472e7'),
       ('신화 속의 세계', '신화와 전설 속으로 당신을 초대합니다.', 'https://via.placeholder.com/150/24f355'),
       ('바다 속 신비', '깊은 바다에서의 모험을 경험하세요.', 'https://via.placeholder.com/150/56a8c2');

INSERT INTO reservation_time (start_at)
VALUES ('09:00'),
       ('12:00'),
       ('17:00'),
       ('21:00');

INSERT INTO member (email, password, name, role)
VALUES ('admin@gmail.com', '$2a$10$MbGFqyn/u4wfggRK7HAqDeC1y9s1mESgmXV3b7e7GZT5u1JkIT.gm', '어드민', 'ADMIN'),
       -- password: abc123
       ('user@gmail.com', '$2a$10$MbGFqyn/u4wfggRK7HAqDeC1y9s1mESgmXV3b7e7GZT5u1JkIT.gm', '유저', 'USER'),
       -- password: abc123
       ('example1@gmail.com', '1234', '구름a', 'USER'),
       ('example2@gmail.com', '1234', '구름b', 'USER'),
       ('example3@gmail.com', '1234', '구름c', 'USER'),
       ('example4@gmail.com', '1234', '구름d', 'USER');

INSERT INTO reservation (date, member_id, time_id, theme_id, status)
VALUES ('2024-04-28', 1, 1, 5, 'ACCEPTED'),
       ('2024-04-28', 2, 2, 5, 'ACCEPTED'),
       ('2024-04-28', 3, 3, 5, 'ACCEPTED'),
       ('2024-04-29', 4, 1, 5, 'ACCEPTED'),
       ('2024-04-29', 1, 2, 5, 'ACCEPTED'),
       ('2024-04-29', 2, 1, 4, 'ACCEPTED'),
       ('2024-04-29', 3, 2, 4, 'ACCEPTED'),
       ('2024-04-29', 4, 3, 4, 'ACCEPTED'),
       ('2024-04-29', 1, 4, 4, 'ACCEPTED'),
       ('2024-05-01', 2, 1, 3, 'ACCEPTED'),
       ('2024-05-01', 3, 2, 3, 'ACCEPTED'),
       ('2024-05-01', 4, 3, 3, 'ACCEPTED'),
       ('2024-05-02', 1, 1, 2, 'ACCEPTED'),
       ('2024-05-02', 2, 2, 2, 'ACCEPTED'),
       ('2024-05-02', 3, 1, 1, 'ACCEPTED'),
       ('2024-05-03', 4, 2, 1, 'ACCEPTED'),
       ('2024-05-03', 1, 3, 1, 'ACCEPTED'),
       ('2024-05-03', 2, 4, 1, 'ACCEPTED'),
       ('2024-05-04', 3, 1, 5, 'ACCEPTED'),
       ('2024-05-04', 4, 2, 4, 'ACCEPTED'),
       ('2024-05-04', 1, 3, 3, 'ACCEPTED'),
       ('2024-05-04', 2, 4, 2, 'ACCEPTED');

INSERT INTO payment (payment_key, account_number, account_holder, bank_name, amount, reservation_id, pay_type)
VALUES ('payment_key1', null, null, null, 10000, 1, 'TOSS_PAY'),
       ('payment_key2', null, null, null, 10000, 2, 'TOSS_PAY'),
       ('payment_key3', null, null, null, 10000, 3, 'TOSS_PAY'),
       ('payment_key4', null, null, null, 10000, 4, 'TOSS_PAY'),
       ('payment_key5', null, null, null, 10000, 5, 'TOSS_PAY'),
       ('payment_key6', null, null, null, 10000, 6, 'TOSS_PAY'),
       ('payment_key7', null, null, null, 10000, 7, 'TOSS_PAY'),
       ('payment_key8', null, null, null, 10000, 8, 'TOSS_PAY'),
       ('payment_key9', null, null, null, 10000, 9, 'TOSS_PAY'),
       ('payment_key10', null, null, null, 10000, 10, 'TOSS_PAY'),
       ('payment_key11', null, null, null, 10000, 11, 'TOSS_PAY'),
       ('payment_key12', null, null, null, 10000, 12, 'TOSS_PAY'),
       ('payment_key13', null, null, null, 10000, 13, 'TOSS_PAY'),
       ('payment_key14', null, null, null, 10000, 14, 'TOSS_PAY'),
       ('payment_key15', null, null, null, 10000, 15, 'TOSS_PAY'),
       ('payment_key16', null, null, null, 10000, 16, 'TOSS_PAY'),
       ('payment_key17', null, null, null, 10000, 17, 'TOSS_PAY'),
       ('payment_key18', null, null, null, 10000, 18, 'TOSS_PAY'),
       ('payment_key19', null, null, null, 10000, 19, 'TOSS_PAY'),
       ('payment_key20', null, null, null, 10000, 20, 'TOSS_PAY'),
       ('payment_key21', null, null, null, 10000, 21, 'TOSS_PAY'),
       (null, '1111-2222-3333', '프린', '국민은행', 10000, 22, 'ACCOUNT_TRANSFER');
