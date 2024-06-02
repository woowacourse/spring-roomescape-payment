insert into member (id, email, password, name, role)
values (1, 'sun@woowa.net', 'password', '썬', 'admin'),
       (2, 'duck@woowa.net', 'password', '오리', 'admin'),
       (3, 'jazz@woowa.net', 'password', '재즈', 'normal'),
       (4, 'mang@woowa.net', 'password', '망쵸', 'normal');

insert into theme (id, name, description, thumbnail)
values (1, '세렌디피티: 뜻밖의 행운',
        '방탈출 게임은 주어진 시간 내에 팀이 퍼즐을 해결하고 탈출하는 것이 목표입니다. 퍼즐은 다양한 형태로 주어질 수 있으며, 팀원들은 상호 협력하여 각종 단서를 찾고 연결하여 문제를 해결해야 합니다. 성공적으로 퍼즐을 풀고 모든 단서를 이용해 탈출하면 게임을 클리어할 수 있습니다.',
        'https://i.postimg.cc/T2Df9mR3/theme-PNG-SERENDIPITY.png'),
       (2, '씨프',
        '방탈출 게임에서는 종종 플레이어가 퍼즐을 풀다가 막히는 경우가 있습니다. 이럴 때 플레이어는 게임 진행자 또는 스탭에게 도움을 요청할 수 있습니다. 그러나 게임이 너무 쉬워지거나 플레이어들이 더 이상 과제에 도전하지 않게 되는 것을 막기 위해, 이러한 힌트나 도움은 일정한 규칙에 따라 제공됩니다. 바로 이 규칙에 따라 플레이어가 힌트를 얻을 수 있는 장소를 "씨프" 또는 "C.P."라고 부릅니다.',
        'https://i.postimg.cc/DyP5kj2p/theme-XX.png'),
       (3, 'SOS',
        '플레이어들이 주어진 시간 안에 퍼즐을 해결하고, 숨겨진 단서를 찾아서 탈출하는 것을 목표로 합니다. 대부분의 경우, 게임은 제한된 공간 안에 설정되어 있으며, 플레이어들은 이 공간에서 다양한 퍼즐을 풀어서 출구를 찾아야 합니다.',
        'https://i.postimg.cc/cLqW2JLB/theme-SOS-SOS.jpg'),
       (4, '데이트 코스 연구회',
        '게임의 시작부터 끝까지, 플레이어들은 로맨틱한 분위기를 느끼며 함께 다양한 퍼즐을 풀고 탈출하기 위해 협력해야 합니다. 일반적으로 "데이트 코스 연구회"의 방탈출 게임은 두 사람 이상의 플레이어가 함께 참여하며, 서로 협력하여 문제를 해결해야 합니다.',
        'https://i.postimg.cc/vDFKqct1/theme.jpg');

insert into reservation_time (id, start_at)
values (1, '10:00'),
       (2, '11:00'),
       (3, '12:00'),
       (4, '13:00'),
       (5, '14:00'),
       (6, '15:00'),
       (7, '16:00'),
       (8, '17:00'),
       (9, '18:00'),
       (10, '19:00'),
       (11, '20:00'),
       (12, '21:00');

insert into reservation (id, member_id, theme_id, date, reservation_time_id, status, created_at, payment_id)
values (1, 1, 1, '2024-06-07', 1, 'RESERVED', '2099-07-01 00:00:00', null),
       (2, 2, 1, '2024-06-07', 1, 'WAITING', '2099-07-01 00:00:02', null),
       (3, 3, 1, '2024-06-07', 1, 'WAITING', '2099-07-01 00:00:03', null),
       (4, 4, 1, '2024-06-07', 1, 'WAITING', '2099-07-01 00:00:04', null),
       (5, 1, 1, '2024-06-08', 2, 'RESERVED', '2099-07-01 00:00:01', null),
       (6, 2, 1, '2024-06-08', 2, 'WAITING', '2099-07-01 00:00:01', null),
       (7, 1, 2, '2024-06-08', 4, 'WAITING', '2099-07-01 00:00:02', null),
       (8, 4, 2, '2024-06-08', 4, 'RESERVED', '2099-07-01 00:00:02', null),
       (9, 1, 1, '2024-06-09', 1, 'RESERVED', '2099-07-01 00:00:03', null),
       (10, 1, 1, '2024-06-09', 1, 'RESERVED', '2099-07-01 00:00:00', null),
       (11, 2, 1, '2024-06-09', 1, 'WAITING', '2099-07-01 00:00:00', null),
       (12, 1, 1, '2024-06-09', 2, 'RESERVED', '2099-07-01 00:00:00', null),
       (13, 1, 3, '2024-06-09', 3, 'RESERVED', '2099-07-01 00:00:00', null),
       (14, 1, 4, '2024-06-09', 3, 'RESERVED', '2099-07-01 00:00:00', null),
       (15, 4, 4, '2024-06-09', 3, 'WAITING', '2099-07-01 00:00:00', null),
       (16, 1, 4, '2024-06-09', 4, 'RESERVED', '2099-07-01 00:00:00', null),
       (17, 1, 3, '2024-06-09', 4, 'RESERVED', '2099-07-01 00:00:00', null),
       (18, 1, 2, '2024-06-09', 4, 'WAITING', '2099-07-01 00:00:00', null);

ALTER TABLE member
    ALTER COLUMN id RESTART WITH 5;
ALTER TABLE theme
    ALTER COLUMN id RESTART WITH 5;
ALTER TABLE reservation_time
    ALTER COLUMN id RESTART WITH 13;
ALTER TABLE reservation
    ALTER COLUMN id RESTART WITH 19;
