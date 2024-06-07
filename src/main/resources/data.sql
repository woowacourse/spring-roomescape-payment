INSERT INTO theme(name, description, thumbnail)
VALUES ('도둑들(스릴러)', '훔치고 달아나라잇..!', 'https://lh3.googleusercontent.com/proxy/UijkDKTrQHQUzR8ykItHuqIn3bc5Vpc-MKOnMuSnEKK5dg43uF4Sj3XosPBTBhVvkMrgtb730f8CVkFVJBEh2E08Bcg1PVsHqdUsaoc'),
       ('학교 탈출(공포)', '학교를 탈출하라..!', 'https://file.miricanvas.com/template_thumb/2022/05/15/13/50/k2nje40j0jwztqza/thumb.jpg'),
       ('미리방 탈출(공포)', '미리미리 탈출하세요', 'https://file.miricanvas.com/template_thumb/2021/07/02/13/20/k4t92g5ntu46etia/thumb.jpg');

INSERT INTO reservation_time(start_at)
VALUES ('15:00:00'),
       ('16:00:00'),
       ('17:00:00');

INSERT INTO member(name, email, password, role)
VALUES ('안돌', 'andole@test.com', '123', 'MEMBER'),
       ('파랑', 'parang@test.com', '123', 'MEMBER'),
       ('리비', 'libienz@test.com', '123', 'MEMBER'),
       ('메이슨', 'mason@test.com', '123', 'MEMBER'),
       ('어드민', 'admin@test.com', '123', 'ADMIN');

INSERT INTO reservation(member_id, date, time_id, theme_id)
VALUES (1, TIMESTAMPADD(DAY, -1, NOW()), '1', '1'),
       (2, TIMESTAMPADD(DAY, -1, NOW()), '1', '1'),
       (3, TIMESTAMPADD(DAY, -1, NOW()), '1', '1'),
       (1, TIMESTAMPADD(DAY, -1, NOW()), '3', '1'),
       (3, TIMESTAMPADD(DAY, -2, NOW()), '1', '2'),
       (2, TIMESTAMPADD(DAY, -1, NOW()), '2', '1'),
       (2, TIMESTAMPADD(DAY, -2, NOW()), '2', '2');

INSERT INTO payment(payment_key, order_id, amount, reservation_id)
VALUES ('dummyPaymentKey', 'dummyId', 20000, '1'),
       ('dummyPaymentKey', 'dummyId', 20000, '2'),
       ('dummyPaymentKey', 'dummyId', 20000, '3'),
       ('dummyPaymentKey', 'dummyId', 20000, '4'),
       ('dummyPaymentKey', 'dummyId', 20000, '5'),
       ('dummyPaymentKey', 'dummyId', 20000, '6'),
       ('dummyPaymentKey', 'dummyId', 20000, '7');




