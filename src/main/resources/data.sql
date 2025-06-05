INSERT INTO member (name, email, password, role)
VALUES ('유저', 'user@email.com', '1234', 'USER'),
       ('미소', 'miso@email.com', '1234', 'USER'),
       ('관리자', 'admin@email.com', '1234', 'ADMIN');

INSERT INTO theme(name, description, thumbnail)
VALUES ('테마1', '설명1', '사진1'),
       ('테마2', '설명2', '사진2'),
       ('테마3', '설명3', '사진3'),
       ('테마4', '설명4', '사진4'),
       ('테마5', '설명5', '사진5'),
       ('테마6', '설명6', '사진6'),
       ('테마7', '설명7', '사진7'),
       ('테마8', '설명8', '사진8'),
       ('테마9', '설명9', '사진9'),
       ('테마10', '설명10', '사진10'),
       ('테마11', '설명11', '사진11');

INSERT INTO reservation_time(start_at)
VALUES ('10:00'),
       ('11:00'),
       ('12:00');

INSERT INTO payment (payment_key, order_id, amount, payment_type)
VALUES ('payKey-1', 'order-1', 10000, '카드'),
       ('payKey-2', 'order-2', 15000, '카카오페이'),
       ('payKey-3', 'order-3', 20000, '카드'),
       ('payKey-4', 'order-4', 18000, '계좌이체');

INSERT INTO reservation(date, time_id, theme_id, member_id, payment_id)
VALUES ('2025-05-18', 1, 1, 1, 1),
       ('2025-05-19', 2, 1, 2, 2),
       ('2025-05-20', 3, 3, 1, 3),
       ('2025-05-21', 1, 1, 1, 4);


INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES ('2025-05-18', 1, 1, 2),
       ('2025-05-18', 1, 1, 3);
