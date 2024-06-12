-- 테마 목록 : 4개
INSERT INTO theme (name, description, thumbnail, CREATE_AT)
VALUES ('공대 탈출', '정신 차려보니 주변엔 체크무늬 셔츠뿐.. 이 곳에서 탈출할 수 있을까?',
        'https://upload2.inven.co.kr/upload/2019/12/13/bbs/i13979925128.jpg',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO theme (name, description, thumbnail, CREATE_AT)
VALUES ('짱구야 아빠를 속인거니?', '올 여름을 강타할 단 하나의 호러 서바이벌 생존 스릴러',
        'https://i.namu.wiki/i/K3QgYrz4Ts2hb4b0-IPf_3hXbhImQR2ICzBIUPrF63c5OhqxOq2KEYKgZe2BbL92c_Omo4gjpNmKWtHnrSRWBg.webp',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO theme (name, description, thumbnail, CREATE_AT)
VALUES ('몽키가든', '원숭지몽. 내가 원숭이 꿈을 꾸는 걸까 원숭이가 내 꿈을 꾸는 걸까',
        'https://image.fmkorea.com/files/attach/new4/20240419/6938511254_494354581_0610c09d6dd2eb271bc1eb79d4bab922.png',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO theme (name, description, thumbnail, CREATE_AT)
VALUES ('시스테마', '시스테마는 러시아의 군용 무술입니다.'
    'https://i.ytimg.com/vi/cGRPXgEH3vc/maxresdefault.jpg',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 예약 시간 목록 : 5개
INSERT INTO reservation_time (start_at, CREATE_AT)
VALUES ('08:00', DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation_time (start_at, CREATE_AT)
VALUES ('10:00', DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation_time (start_at, CREATE_AT)
VALUES ('13:00', DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation_time (start_at, CREATE_AT)
VALUES ('21:00', DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation_time (start_at, CREATE_AT)
VALUES ('23:00', DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 유저 목록 : 2개
INSERT INTO member(name, email, password, role, CREATE_AT)
VALUES ('member', 'email@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO member(name, email, password, role, CREATE_AT)
VALUES ('admin', 'email2@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'ADMIN',
        DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 테마 1 예약 목록 : 4개
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 1, 1, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 2, 1, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 3, 1, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 4, 1, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 테마 2 예약 목록 : 2개
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 1, 2, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 2, 2, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 테마 3 예약 목록 : 1개
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 1, 3, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- 테마 4 예약 목록 : 3개
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 1, 4, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 2, 4, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, CREATE_AT)
VALUES (DATEADD('DAY', -3, CURRENT_DATE), 3, 4, 1, DATEADD('DAY', -3, CURRENT_TIMESTAMP));
