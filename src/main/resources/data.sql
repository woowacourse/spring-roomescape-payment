/**
  member data
 */
INSERT INTO member (role, password, name, email)
VALUES ('ADMIN', 'N6UX5zCeUl/v6khTHKEmTRG/qLZNyrirpjySbDj+Tc0=', '관리자', 'admin@mail.com');  /** adminPw1234! */
INSERT INTO member (role, password, name, email)
VALUES ('USER', 'c7c8DLfgOv4RFWUd7q9VDn1684F5dTghVOXoAzrc1GA=', '일반 회원', 'user@mail.com');   /** userPw1234! */
INSERT INTO member (role, password, name, email)
VALUES ('USER', 'c7c8DLfgOv4RFWUd7q9VDn1684F5dTghVOXoAzrc1GA=', '켈리', 'kelly@mail.com');   /** userPw1234! */
INSERT INTO member (role, password, name, email)
VALUES ('USER', 'c7c8DLfgOv4RFWUd7q9VDn1684F5dTghVOXoAzrc1GA=', '테바', 'teva@mail.com');   /** userPw1234! */
INSERT INTO member (role, password, name, email)
VALUES ('USER', 'c7c8DLfgOv4RFWUd7q9VDn1684F5dTghVOXoAzrc1GA=', '파랑', 'blue@mail.com');   /** userPw1234! */

/**
  theme data
 */
INSERT INTO theme(name, description, thumbnail)
VALUES ('테바와 비밀친구', '나랑.. 비밀친구할래..?', 'https://i.ytimg.com/vi/On3nNUVvu4M/maxresdefault.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('켈리의 댄스교실', '켈켈켈켈켈', 'https://img.biz.sbs.co.kr/upload/2023/03/30/wjW1680139541589-850.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('우테코 탈출하기', '우테코를... 탈출..하자..!', 'https://user-images.githubusercontent.com/20608121/69005067-ce8b4c00-095f-11ea-956e-cfb07e30cb30.png');
INSERT INTO theme(name, description, thumbnail)
VALUES ('네오의 두근두근 피드백 강의', '??? : 네오가 setter 쓰라고 했는데요?', 'https://wimg.mk.co.kr/meet/neds/2021/12/image_readtop_2021_1148189_16400449024890188.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('리사의 소프트 교육', '에궁..ㅜㅜ', '공감하는 리사 사진');
INSERT INTO theme(name, description, thumbnail)
VALUES ('신천직화 탈출하기', '저희 3인분 시켰는데 왜 계란말이 안주세요?', '제육 사진');
INSERT INTO theme(name, description, thumbnail)
VALUES ('장미상가 탈출하기', '여기 A동 아니에요?', '장미상가 사진');
INSERT INTO theme(name, description, thumbnail)
VALUES ('페드로의 주먹', '페급~ (페드로 급이라는 뜻~)', '페드로 사진');
INSERT INTO theme(name, description, thumbnail)
VALUES ('사물함 탈취하기', '니 사물함 쩔더라 ㅋ', '사물함 사진');
INSERT INTO theme(name, description, thumbnail)
VALUES ('아루의 입기타', '뚜루루루룰루루루룰', 'https://mblogthumb-phinf.pstatic.net/MjAyMDAzMjNfMjY4/MDAxNTg0OTI0MDg4NDg1.mF8ebF1dybaR-EPQ7PxEpupIes6auq93ITImZrT6u2Ug.XHQikRjihAp5pwkQM_yvw7wGD8wlxStaVH7wn3Yn_NMg.JPEG.chopste11/1584924089072.jpg?type=w800');
INSERT INTO theme(name, description, thumbnail)
VALUES ('이든의 프로틴 쉐이크 제작 강의', '내 단백질 어디있죠?', 'https://image.edaily.co.kr/images/Photo/files/NP/S/2020/06/PS20062400239.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('솔라의 솔라빔', '자라나라 머리머리!', 'https://i.namu.wiki/i/z3gLK6V6DXqH0icCfe1LAailWttZDL0aZW-m4a6B4Vazrxyv4PdIbL9w_angH_uHCx92mqzkOvFFjCoDSGnFsg.webp');
INSERT INTO theme(name, description, thumbnail)
VALUES ('포비의 긴급 포수타', '포비는 크루들에게 실망했다.', 'https://i.namu.wiki/i/kbZH5JV_XOwQipJa6b--LfXq-lQTBacYWUE97S1bTgzUwwD5smdLrPBdTa5WPxKsWWKsFgkVQe5WOIDFdzmmNA.webp');
INSERT INTO theme(name, description, thumbnail)
VALUES ('브리와 솔라의 페어프로그래밍 연극', '... 이거 이렇게 짜는거 맞아요?', 'https://www.jeollailbo.com/news/photo/201612/501462_16626_3523.png');
INSERT INTO theme(name, description, thumbnail)
VALUES ('레디의 코드리뷰', '아씨 깜짝아! 내 코드인줄 알았네 (제제의 코드를 보며)', '레디 사진');

/**
  reservation time data
 */
INSERT INTO reservation_time(start_at)
VALUES ('09:30');
INSERT INTO reservation_time(start_at)
VALUES ('11:30');
INSERT INTO reservation_time(start_at)
VALUES ('13:30');
INSERT INTO reservation_time(start_at)
VALUES ('15:30');
INSERT INTO reservation_time(start_at)
VALUES ('17:30');
INSERT INTO reservation_time(start_at)
VALUES ('19:30');
INSERT INTO reservation_time(start_at)
VALUES ('21:30');
INSERT INTO reservation_time(start_at)
VALUES ('23:30');

/**
  reservation
 */
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (2, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -3, NOW()) AS DATE), 1, 1);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (3, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -4, NOW()) AS DATE), 1, 2);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (4, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -2, NOW()) AS DATE), 4, 1);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (5, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -6, NOW()) AS DATE), 3, 1);

INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (1, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -3, NOW()) AS DATE), 5, 1);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (2, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -4, NOW()) AS DATE), 7, 2);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (3, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -2, NOW()) AS DATE), 6, 13);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (4, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -6, NOW()) AS DATE), 8, 14);

INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (5, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -3, NOW()) AS DATE), 1, 2);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (3, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -4, NOW()) AS DATE), 1, 12);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (2, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -2, NOW()) AS DATE), 4, 10);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (1, 'RESERVATION', CAST(TIMESTAMPADD(DAY, -6, NOW()) AS DATE), 3, 11);

INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (2, 'RESERVATION', CAST(TIMESTAMPADD(DAY, 3, NOW()) AS DATE), 1, 10);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (4, 'RESERVATION', CAST(TIMESTAMPADD(DAY, 4, NOW()) AS DATE), 1, 15);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (1, 'RESERVATION', CAST(TIMESTAMPADD(DAY, 2, NOW()) AS DATE), 4, 9);
INSERT INTO reservation(member_id, status, date, time_id, theme_id)
VALUES (5, 'RESERVATION', CAST(TIMESTAMPADD(DAY, 6, NOW()) AS DATE), 3, 8);

/**
  Reservation Waiting
 */
INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (1, CAST(TIMESTAMPADD(DAY, 3, NOW()) AS DATE), 1, 10, CAST(TIMESTAMPADD(MINUTE, 2, CURRENT_TIMESTAMP()) AS DATETIME));
INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (3, CAST(TIMESTAMPADD(DAY, 3, NOW()) AS DATE), 1, 10, CAST(TIMESTAMPADD(MINUTE, 3, CURRENT_TIMESTAMP()) AS DATETIME));
INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (4, CAST(TIMESTAMPADD(DAY, 3, NOW()) AS DATE), 1, 10, CAST(TIMESTAMPADD(MINUTE, 5, CURRENT_TIMESTAMP()) AS DATETIME));

INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (1, CAST(TIMESTAMPADD(DAY, 6, NOW()) AS DATE), 3, 8, CAST(TIMESTAMPADD(MINUTE, 6, CURRENT_TIMESTAMP()) AS DATETIME));
INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (3, CAST(TIMESTAMPADD(DAY, 6, NOW()) AS DATE), 3, 8, CAST(TIMESTAMPADD(MINUTE, 50, CURRENT_TIMESTAMP()) AS DATETIME));
INSERT INTO reservation_waiting(member_id, date, time_id, theme_id, created_at)
VALUES (2, CAST(TIMESTAMPADD(DAY, 6, NOW()) AS DATE), 3, 8, CAST(TIMESTAMPADD(MINUTE, 1, CURRENT_TIMESTAMP()) AS DATETIME));

/**
  Payment History
 */

INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (1, 'gen_test_payment_key_1', 'test_order_name', 'DONE', 30000, '토스페이', NOW());
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (2, 'gen_test_payment_key_2', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (3, 'gen_test_payment_key_3', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (4, 'gen_test_payment_key_4', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (5, 'gen_test_payment_key_5', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (6, 'gen_test_payment_key_6', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (7, 'gen_test_payment_key_7', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (8, 'gen_test_payment_key_8', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (9, 'gen_test_payment_key_9', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (10, 'gen_test_payment_key_10', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (11, 'gen_test_payment_key_11', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (12, 'gen_test_payment_key_12', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (13, 'gen_test_payment_key_13', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (14, 'gen_test_payment_key_14', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (15, 'gen_test_payment_key_15', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
INSERT INTO payment_history(reservation_id, payment_key, order_name, payment_status, total_amount, payment_provider, approved_at)
VALUES (16, 'gen_test_payment_key_16', 'test_order_name', 'DONE', 30000, '토스페이', CAST(TIMESTAMPADD(DAY, 0, NOW()) AS DATETIME));
