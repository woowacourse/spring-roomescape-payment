INSERT INTO reservation_time(start_at)
VALUES ('10:00:00');
INSERT INTO reservation_time(start_at)
VALUES ('19:00:00');

INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨2 탈출', '우테코 레벨2 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨3 탈출', '우테코 레벨3 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');
INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨4 탈출', '우테코 레벨4 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO member(name, email, role, password)
VALUES ('관리자', 'admin@abc.com', 'ADMIN', '1234');
INSERT INTO member(name, email, role, password)
VALUES ('브리', 'bri@abc.com', 'USER', '1234');
INSERT INTO member(name, email, role, password)
VALUES ('브라운', 'brown@abc.com', 'USER', '1234');
INSERT INTO member(name, email, role, password)
VALUES ('오리', 'duck@abc.com', 'USER', '1234');

INSERT INTO schedule (date, time_id, theme_id)
VALUES (CURRENT_DATE - 1, 1, 1);
INSERT INTO schedule (date, time_id, theme_id)
VALUES (CURRENT_DATE - 2, 1, 1);
INSERT INTO schedule (date, time_id, theme_id)
VALUES (CURRENT_DATE - 2, 2, 2);
INSERT INTO schedule (date, time_id, theme_id)
VALUES (CURRENT_DATE + 1, 1, 2);

INSERT INTO reservation (member_id, schedule_id, status)
VALUES (2, 1, 'ADMIN_RESERVE');
INSERT INTO reservation (member_id, schedule_id, status)
VALUES (1, 2, 'ADMIN_RESERVE');
INSERT INTO reservation (member_id, schedule_id, status)
VALUES (3, 3, 'ADMIN_RESERVE');
INSERT INTO reservation (member_id, schedule_id, status)
VALUES (2, 4, 'ADMIN_RESERVE');

INSERT INTO waiting (schedule_id, member_id, created_at)
VALUES (4, 3, '2024-05-20 12:10:00.000');
INSERT INTO waiting (schedule_id, member_id, created_at)
VALUES (4, 4, '2024-05-20 12:20:00.000');
