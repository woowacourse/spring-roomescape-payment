INSERT INTO reservation_time(start_at)
VALUES ('10:00:00');

INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨2 탈출', '우테코 레벨2 탈출기!', 'https://img.jpg');

INSERT INTO reservation (member_id, date, time_id, theme_id)
VALUES (1, CURRENT_DATE - 1, 1, 1);
