insert into member (id, name, email, password, role)
values ('1', '짱구', 'email1@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('2', '아마', 'email2@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('3', '포라', 'email3@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('4', '한스', 'email4@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('5', '띠용', 'email5@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('6', '오리', 'email6@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');
insert into member (id, name, email, password, role)
values ('7', '관리자', 'admin@domain.com', '$2a$10$YhpY6/Ob2HtUbfUTzw66TupYcxmbMnhHLpLAXwuxAiYN2r0.sGJKq', 'USER');

insert into theme (id, name, description, thumbnail)
values ('1', '콜러', '나는 기억을 잃었다.',
        'https://mblogthumb-phinf.pstatic.net/MjAyMTA1MDdfNDYg/MDAxNjIwMzE1MDk1Njc0.9ab4XQbrio5hA-H1OkeIx6Y7mlVKPJazXQLBYAbwt9Mg.Pq6lzfxJN1xjlvVN9tSFyWkFDrx2Uc5tmm6NdkYEiR0g.JPEG.dragonyoo/038_%EC%BD%9C%EB%9F%AC.jpg?type=w800');
insert into theme (id, name, description, thumbnail)
values ('2', '루시드드림',
        '현실과 구분이 되지 않는 꿈. 그 안에서 당신은 처음보는 소녀의 부름에 낯선 방에 이르게 된다. 자신을 J라 소개하는 그녀 J는 자신의 이야기를 들어달라 해놓고는 홀연히 사려져버린다. "J...? 4년 전 연쇄실종사건의 마지막 실종자...J? 단순한 꿈이 아님을 직감적으로 눈치챈 당신은 천천히 방을 둘러보게 되는데....',
        'https://mblogthumb-phinf.pstatic.net/MjAyMTA1MDdfNTgg/MDAxNjIwMzEzMzY2OTA3.eG9QFBHifh3uHSL6X8mhu0EYC1Tb9I8WNDAUdC005Ekg.HPHNxs98suVcr-Mb-GJn5N6xr-4m-ghNjVDuLABXM4kg.JPEG.dragonyoo/007_%EB%A3%A8%EC%8B%9C%EB%93%9C_%EB%93%9C%EB%A6%BC.jpg?type=w800');
insert into theme (id, name, description, thumbnail)
values ('3', 'LIVE', 'BJ 라이브와 함께 방송하고 싶다면 언제나 찾아와주세요.',
        'https://mblogthumb-phinf.pstatic.net/MjAyMTA1MDdfNzMg/MDAxNjIwMzE4NDIzMzE4.lyar-G4LRBgSyI1aSw0PLraoYPW4LQKF5cijHqbkPl8g.jMOQPyqFEQSmxfmgT5I7u-JE8-fQCWx2vxzG_FfqlLog.JPEG.dragonyoo/113_LIVE.jpg?type=w800');
insert into theme (id, name, description, thumbnail)
values ('4', '소식', '기다려 왔던 소식이 왔어요.',
        'https://mblogthumb-phinf.pstatic.net/MjAyMTA1MDVfMjcz/MDAxNjIwMjE5ODQ4MzAy.C70DWEQ5t8_B1GiRodUEZ2D39UpK33235B-gBxKeXtkg.S88PtEvyJYsrc6M5qBTC5bGbrILqTESRnVL6IVnabQUg.JPEG.dragonyoo/8.jpg?type=w800');
insert into theme (id, name, description, thumbnail)
values ('5', 'CIA본부에서의 탈출',
        '이것은 함정인가? 방을 나가기 전 요원들의 표정을 봐서는 아닌듯 하다. 심지어 취조실 문도 아직 잠겨있다. 그렇다면 이것은 기회다! 요원들이 돌아오기 전에 어서 탈출하자!',
        'https://mblogthumb-phinf.pstatic.net/MjAyMTA1MDVfMTAw/MDAxNjIwMjIwODM0MTM2.v9_VTttDrvK_m8FceBIuHBPZyeolYX6h3xHtjYvJhO8g.osNlhlu__LSlEnr37n4CG0sarpm7_EPp2jUkekU70WUg.PNG.dragonyoo/11.png?type=w800');

insert into time_slot (id, start_at)
values ('1', '09:00');
insert into time_slot (id, start_at)
values ('2', '11:00');
insert into time_slot (id, start_at)
values ('3', '16:00');

insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('1', 1, 1, DATEADD('DAY', -1, CURRENT_DATE), 1);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('2', 1, 1, DATEADD('DAY', -2, CURRENT_DATE), 1);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('3', 1, 1, DATEADD('DAY', -3, CURRENT_DATE), 1);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('4', 2, 1, DATEADD('DAY', -1, CURRENT_DATE), 1);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('5', 2, 2, DATEADD('DAY', -2, CURRENT_DATE), 2);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('6', 3, 2, DATEADD('DAY', -3, CURRENT_DATE), 2);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('7', 4, 2, DATEADD('DAY', -1, CURRENT_DATE), 2);
insert into reservation (id, theme_id, member_id, date, time_slot_id)
values ('8', 5, 2, DATEADD('DAY', -2, CURRENT_DATE), 2);

INSERT INTO payment (id, payment_key, amount, order_id, reservation_id, status)
VALUES ('1', 'paymentKey1', 200000, 'orderId1', 1, 'DONE'),
       ('2', 'paymentKey2', 200000, 'orderId2', 2, 'DONE'),
       ('3', 'paymentKey3', 200000, 'orderId3', 3, 'DONE'),
       ('4', 'paymentKey4', 200000, 'orderId4', 4, 'DONE'),
       ('5', 'paymentKey5', 200000, 'orderId5', 5, 'DONE'),
       ('6', 'paymentKey6', 200000, 'orderId6', 6, 'DONE'),
       ('7', 'paymentKey7', 200000, 'orderId7', 7, 'DONE'),
       ('8', 'paymentKey8', 200000, 'orderId8', 8, 'DONE');