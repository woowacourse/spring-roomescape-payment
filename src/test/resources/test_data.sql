INSERT INTO reservation_time (start_at)
VALUES ('10:00');
INSERT INTO reservation_time (start_at)
VALUES ('11:00');

INSERT INTO member (name, role, email, password)
VALUES ('썬', 'MEMBER', 'sun@email.com', '1111');
INSERT INTO member (name, role, email, password)
VALUES ('배키', 'MEMBER', 'dmsgml@email.com', '2222');

INSERT INTO theme (name, description, thumbnail)
VALUES ('name1', 'description1', 'thumbnail1');
INSERT INTO theme (name, description, thumbnail)
VALUES ('name2', 'description2', 'thumbnail2');
INSERT INTO theme (name, description, thumbnail)
VALUES ('name3', 'description3', 'thumbnail3');

INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 1, 1);
INSERT INTO reservation (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 2, 1);

INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 1, 2);
INSERT INTO waiting (date, time_id, theme_id, member_id)
VALUES (CURRENT_DATE + 1, 1, 2, 2);

INSERT INTO payment (payment_key, amount, reservation_id)
VALUES ('test', 1999999, 1);
INSERT INTO payment (payment_key, amount, reservation_id)
VALUES ('test2', 1999999, 2);
