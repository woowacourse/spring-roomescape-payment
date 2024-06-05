INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 1', '설명 1', 'url 1');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 2', '설명 2', 'url 2');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마 3', '설명 3', 'url 3');

INSERT INTO reservation_time (start_at)
VALUES ('12:00');
INSERT INTO reservation_time (start_at)
VALUES ('13:00');
INSERT INTO reservation_time (start_at)
VALUES ('14:00');
INSERT INTO reservation_time (start_at)
VALUES ('15:00');

INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('admin', 'ADMIN', 'admin@email.com', 'password');
INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('아서', 'USER', 'Hyunta@wooteco.com', 'KingArthur');
INSERT INTO member (NAME, ROLE, EMAIL, PASSWORD)
VALUES ('제이미', 'USER', 'jamie9504@wooteco.com', 'jamie9504');

INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 1, 1, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 2, 1, 2, '2024-04-02T11:20', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 1, 1, 3, '2024-04-02T12:30', 'WAITING');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 3, 1, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 4, 2, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 3, 1, 3, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 2, 1, 1, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE - 1, 1, 1, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE, 1, 1, 1, '2024-04-02', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE + 1, 1, 2, 1, '2024-04-02T10:30', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE + 1, 1, 2, 2, '2024-04-02T10:40', 'WAITING');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE + 2, 1, 2, 1, '2024-04-02T10:30', 'BOOKED');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE + 2, 1, 2, 2, '2024-04-02T11:30', 'WAITING');
INSERT INTO reservation (date, time_id, theme_id, member_id, created_at, reservation_status)
VALUES (CURRENT_DATE + 2, 1, 2, 3, '2024-04-02T12:30', 'WAITING');
