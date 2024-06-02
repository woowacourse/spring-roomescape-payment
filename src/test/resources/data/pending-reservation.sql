INSERT INTO theme (name, description, thumbnail)
VALUES ('테마1', '재밌는 테마입니다', 'https://cdn.pixabay.com/photo/2016/01/22/11/50/live-escape-game-1155620_1280.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('08:00');

INSERT INTO member (name, email, password, role)
VALUES ('member1', 'email1@email.com', 'f6f2ea8f45d8a057c9566a33f99474da2e5c6a6604d736121650e2730c6fb0a3', 'MEMBER');

INSERT INTO payment (amount, order_id, payment_key)
VALUES (10000, 'WTEST000001', 'qwerasdfzxcv');

INSERT INTO reservation (date, time_id, theme_id, reservation_member_id, payment_id, status)
VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 1, 1, 'PENDING');
