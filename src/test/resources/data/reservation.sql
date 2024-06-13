SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO theme (name, description, thumbnail)
VALUES ('테마', '재밌는 테마입니다', 'https://cdn.pixabay.com/photo/2016/01/22/11/50/live-escape-game-1155620_1280.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('11:00');

INSERT INTO member (name, email, password, role)
VALUES ('member', 'email1@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER'),
       ('admin', 'admin@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'ADMIN');
