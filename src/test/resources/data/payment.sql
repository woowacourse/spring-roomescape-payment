
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO theme (name, description, thumbnail)
VALUES ('테마1', '재밌는 테마입니다', 'https://cdn.pixabay.com/photo/2016/01/22/11/50/live-escape-game-1155620_1280.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('08:00'),
       ('10:00'),
       ('12:00');

INSERT INTO member (name, email, password, role)
VALUES ('member1', 'email1@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER'),
       ('member2', 'email2@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER'),
       ('member3', 'email3@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER');

INSERT INTO payment (amount, order_id, payment_key)
VALUES (10000, 'WTEST000001', 'qwerasdfzxcv');

INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, payment_id, status)
VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 1, 1, 'RESERVED_COMPLETE'),
       (DATEADD('DAY', 1, CURRENT_DATE), 2, 1, 2, null, 'RESERVED_UNPAID'),
       (DATEADD('DAY', 1, CURRENT_DATE), 3, 1, 3, null, 'PENDING');
