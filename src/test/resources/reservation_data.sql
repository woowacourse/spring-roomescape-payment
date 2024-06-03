INSERT INTO reservation_time (start_at)
VALUES ('10:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('name1', 'description1', 'thumbnail1');
INSERT INTO theme (name, description, thumbnail)
VALUES ('name2', 'description2', 'thumbnail2');

INSERT INTO member (name, role, email, password)
VALUES ('썬', 'MEMBER', 'sun@email.com', '1111');
INSERT INTO member (name, role, email, password)
VALUES ('배키', 'MEMBER', 'dmsgml@email.com', '2222');

INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 2, 1, 1, 2);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 2, 1, 2, 2);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 4, 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 4, 1, 2, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 5, 1, 1, 2);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 5, 1, 2, 2);
