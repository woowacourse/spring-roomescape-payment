-- 회원
INSERT INTO member (name, email, password, role)
VALUES
('카키', 'kaki@email.com', '1234', 'USER'),
('어드민', 'admin@email.com', '1234', 'ADMIN'),
('솔라', 'solar@email.com', '1234', 'USER'),
('브라운', 'brown@email.com', '1234', 'USER'),
('네오', 'neo@email.com', '1234', 'USER'),
('브리', 'bre@email.com', '1234', 'USER'),
('포비', 'pobi@email.com', '1234', 'USER'),
('구구', 'googoo@email.com', '1234', 'USER'),
('토미', 'tomi@email.com', '1234', 'USER'),
('리사', 'risa@email.com', '1234', 'USER');
ALTER TABLE member ALTER COLUMN id RESTART WITH 11;

-- 에약 시간
INSERT INTO reservation_time (start_at)
VALUES
('10:00'),
('10:30'),
('11:00'),
('11:30'),
('12:00'),
('12:30'),
('13:00'),
('13:30'),
('14:00'),
('14:30');
ALTER TABLE reservation_time ALTER COLUMN id RESTART WITH 11;

-- 테마
INSERT INTO theme (name, description, thumbnail)
VALUES
('공포', '무서워요', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('SF', '미래', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('원숭이 사원', '원숭이들의 공격', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('나가야 산다', '빨리 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('좀비 사태', '좀비들의 공격', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('공포의 놀이공원', '놀이공원 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('지하실', '지하실 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('타이타닉', '타이타닉에서 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
('미술관을 털어라', '미술관을 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '바이러스', '바이러스를 막으세요', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '프리즌 브레이크', '감옥을 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '아즈텍 신전', '신전을 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '우주 정거장', '우주 정거장을 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '치과의사', '치과의사를 피해 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
( '비밀요원', '비밀요원이 돼 탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
ALTER TABLE theme ALTER COLUMN id RESTART WITH 16;

-- 예약
INSERT INTO reservation (member_id, date, time_id, theme_id, status, created_at)
VALUES
(1, CURRENT_DATE, 1, 1, 'SUCCESS', CURRENT_TIMESTAMP),
(2, CURRENT_DATE, 1, 2, 'SUCCESS', CURRENT_TIMESTAMP),
(3, CURRENT_DATE, 1, 3, 'SUCCESS', CURRENT_TIMESTAMP),
(4, CURRENT_DATE, 1, 4, 'SUCCESS', CURRENT_TIMESTAMP),
(5, CURRENT_DATE, 1, 5, 'SUCCESS', CURRENT_TIMESTAMP),
(6, CURRENT_DATE, 1, 6, 'SUCCESS', CURRENT_TIMESTAMP),
(7, CURRENT_DATE, 1, 7, 'SUCCESS', CURRENT_TIMESTAMP),
(8, CURRENT_DATE, 1, 8, 'SUCCESS', CURRENT_TIMESTAMP),
(9, CURRENT_DATE, 1, 9, 'SUCCESS', CURRENT_TIMESTAMP),
(1, CURRENT_DATE, 1, 9, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '1' MINUTE),
(2, CURRENT_DATE, 1, 9, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '2' MINUTE),
(1, DATEADD('DAY', 1, CURRENT_DATE), 10, 10, 'SUCCESS', CURRENT_TIMESTAMP),
(2, DATEADD('DAY', 1, CURRENT_DATE), 1, 11, 'SUCCESS', CURRENT_TIMESTAMP),
(3, DATEADD('DAY', 1, CURRENT_DATE), 2, 12, 'SUCCESS', CURRENT_TIMESTAMP),
(1, DATEADD('DAY', 1, CURRENT_DATE), 2, 12, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '1' MINUTE),
(2, DATEADD('DAY', 1, CURRENT_DATE), 2, 12, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '2' MINUTE),
(4, DATEADD('DAY', 1, CURRENT_DATE), 3, 13, 'SUCCESS', CURRENT_TIMESTAMP),
(5, DATEADD('DAY', 1, CURRENT_DATE), 4, 14, 'SUCCESS', CURRENT_TIMESTAMP),
(1, DATEADD('DAY', 2, CURRENT_DATE), 5, 15, 'SUCCESS', CURRENT_TIMESTAMP),
(2, DATEADD('DAY', 2, CURRENT_DATE), 6, 1, 'SUCCESS', CURRENT_TIMESTAMP),
(3, DATEADD('DAY', 2, CURRENT_DATE), 7, 2, 'SUCCESS', CURRENT_TIMESTAMP),
(4, DATEADD('DAY', 2, CURRENT_DATE), 8, 3, 'SUCCESS', CURRENT_TIMESTAMP),
(1, DATEADD('DAY', 3, CURRENT_DATE), 9, 4, 'SUCCESS', CURRENT_TIMESTAMP),
(2, DATEADD('DAY', 3, CURRENT_DATE), 10, 5, 'SUCCESS', CURRENT_TIMESTAMP),
(3, DATEADD('DAY', 3, CURRENT_DATE), 1, 6, 'SUCCESS', CURRENT_TIMESTAMP),
(1, DATEADD('DAY', 4, CURRENT_DATE), 2, 7, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '1' MINUTE),
(2, DATEADD('DAY', 4, CURRENT_DATE), 2, 7, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '2' MINUTE),
(2, DATEADD('DAY', 4, CURRENT_DATE), 3, 1, 'SUCCESS', CURRENT_TIMESTAMP),
(1, DATEADD('DAY', 5, CURRENT_DATE), 4, 1, 'SUCCESS', CURRENT_TIMESTAMP),
(2, DATEADD('DAY', 5, CURRENT_DATE), 4, 1, 'WAIT', CURRENT_TIMESTAMP + INTERVAL '1' MINUTE);
ALTER TABLE reservation ALTER COLUMN id RESTART WITH 31;

INSERT INTO payment (payment_key, order_id, total_amount, status, reservation_id)
VALUES
    ('payment_key1','order_id1',1000,'DONE',1),
    ('payment_key2','order_id2',1000,'DONE',2),
    ('payment_key3','order_id3',1000,'DONE',3),
    ('payment_key4','order_id4',1000,'DONE',4),
    ('payment_key5','order_id5',1000,'DONE',5),
    ('payment_key6','order_id6',1000,'DONE',6),
    ('payment_key7','order_id7',1000,'DONE',7),
    ('payment_key8','order_id8',1000,'DONE',8),
    ('payment_key9','order_id9',1000,'DONE',9),
    ('payment_key12','order_id12',1000,'DONE',12),
    ('payment_key13','order_id13',1000,'DONE',13),
    ('payment_key14','order_id14',1000,'DONE',14),
    ('payment_key17','order_id17',1000,'DONE',17),
    ('payment_key18','order_id18',1000,'DONE',18),
    ('payment_key19','order_id19',1000,'DONE',19),
    ('payment_key20','order_id20',1000,'DONE',20),
    ('payment_key21','order_id21',1000,'DONE',21),
    ('payment_key22','order_id22',1000,'DONE',22),
    ('payment_key23','order_id23',1000,'DONE',23),
    ('payment_key24','order_id24',1000,'DONE',24),
    ('payment_key25','order_id25',1000,'DONE',25),
    ('payment_key28','order_id28',1000,'DONE',28),
    ('payment_key29','order_id29',1000,'DONE',29);
