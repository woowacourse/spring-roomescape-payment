INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'admin@email.com', '1', 'ADMIN'),
       ('벨로', 'normal@email.com', '1', 'NORMAL'),
       ('지훈', 'jihun@email.com', '1', 'NORMAL'),
       ('서연', 'seoyeon@email.com', '1', 'NORMAL'),
       ('민준', 'mj@email.com', '1', 'NORMAL'),
       ('하영', 'ha0@email.com', '1', 'NORMAL'),
       ('예진', 'yejin@email.com', '1', 'NORMAL'),
       ('현우', 'hyeonwoo@email.com', '1', 'NORMAL'),
       ('채영', 'chae0@email.com', '1', 'NORMAL'),
       ('도윤', 'dodo@email.com', '1', 'NORMAL'),
       ('수민', 'sooooming@email.com', '1', 'NORMAL'),
       ('지아', 'zia@email.com', '1', 'NORMAL'),
       ('정우', 'jeongWoo@email.com', '1', 'NORMAL'),
       ('다현', 'dahyeon123@email.com', '1', 'NORMAL'),
       ('하늘', 'kimsky@email.com', '1', 'NORMAL');

INSERT INTO reservation_time (start_at)
VALUES ('12:00'),
       ('14:00'),
       ('16:00'),
       ('18:00'),
       ('20:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('폐교의 비밀', '1970년대 폐쇄된 학교에서 벌어지는 공포 미스터리를 풀어야 한다.',
        'https://thumb.photo-ac.com/fb/fb4b935f76db9ec1c4b4b28d5e33d2fa_t.jpeg'),
       ('사라진 연구소', '기이한 실험이 벌어졌던 연구소에서 탈출을 시도하라.',
        'https://png.pngtree.com/background/20230527/original/pngtree-this-is-the-view-of-the-dark-picture-image_2765374.jpg'),
       ('시간여행자의 저택', '1910년대로 타임슬립한 당신, 과거를 바꿔야 미래가 산다.',
        'https://cdn.pixabay.com/photo/2023/09/20/18/30/ai-generated-8265368_1280.jpg'),
       ('암호화된 유산', '숨겨진 재산을 찾기 위해 조상의 흔적을 해독해야 한다.',
        'https://www.unescoicdh.org/upload/bbs/00000063/2022/1643964396-1258-3.jpg'),
       ('고대 신전의 저주', '탐험 도중 고대 신전의 저주에 갇힌 당신의 생존기.',
        'https://cdn.pixabay.com/photo/2017/02/07/21/25/acropolis-2047093_1280.jpg'),
       ('소실된 기억', '기억을 잃은 채 눈을 뜬 방, 당신의 정체를 되찾아라.',
        'https://cdn.pharmnews.com/news/photo/202410/251718_129207_453.jpg'),
       ('하늘정원의 비밀', '모두가 사랑한 하늘정원, 그곳에 숨겨진 진실은?',
        'https://img.freepik.com/premium-vector/skyland_944197-30.jpg'),
       ('무인도 생존 게임', '사라진 무인도에서 살아남기 위한 협동과 추리 게임.',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsTD70hvbxqlzepN9VeVnDIpsvJe4735W9bg&s'),
       ('007 작전: 이중 스파이', '첩보원으로 위장한 당신, 이중 스파이를 색출하라.',
        'https://i.pinimg.com/736x/72/20/03/7220035f57fff42d5d30bf938ad4f72a.jpg'),
       ('사라진 그림자의 도시', '빛이 사라진 도시에서 진실을 밝히는 여정.',
        'https://ledibond.com/wp-content/uploads/2023/03/20210111_174014-scaled.jpg'),
       ('유령 열차의 비밀', '밤마다 운행되는 유령 열차, 그 끝에는 무엇이 있을까?',
        'https://images.pexels.com/photos/3156836/pexels-photo-3156836.jpeg'),
       ('마법학교 최후의 시험', '마법사가 되기 위한 마지막 시험, 당신의 선택은?',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnDgp5mhHyCUSgA5n8uFGRn-E6E8SSkYwsZA&s');

INSERT INTO reservation (member_id, date, time_id, theme_id, status, created_at, updated_at)
VALUES (2, DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 'RESERVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 벨로
       (2, DATEADD('DAY', 2, CURRENT_DATE), 2, 2, 'RESERVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 벨로
       (2, DATEADD('DAY', 3, CURRENT_DATE), 3, 3, 'RESERVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 벨로
       (2, DATEADD('DAY', 4, CURRENT_DATE), 4, 4, 'RESERVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), -- 벨로
       (3, DATEADD('DAY', 5, CURRENT_DATE), 1, 5, 'RESERVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); -- 지훈

INSERT INTO waiting(member_id, date, reservation_time_id, theme_id, created_at, updated_at)
VALUES (2, DATEADD('DAY', 5, CURRENT_DATE), 1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO payment (payment_type)
VALUES ('TOSS'),
       ('TOSS'),
       ('TOSS'),
       ('ADMIN'),
       ('TOSS');

INSERT INTO toss_payment (payment_key, order_id, amount, payment_status, created_at, updated_at)
VALUES ('payment_key_for_test1', 'order_1', 50000, 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('payment_key_for_test2', 'order_2', 50000, 'FAILED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('payment_key_for_test3', 'order_3', 50000, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('payment_key_for_test4', 'order_4', 50000, 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO admin_payment (payment_id, admin_id)
VALUES (4, 1);

INSERT INTO reservation_payment (reservation_id, payment_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);
