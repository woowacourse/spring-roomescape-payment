INSERT INTO theme (name, description, thumbnail)
VALUES ('테마1', '재밌음', '/image/default.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마2', '무서움', '/image/default.jpg');
INSERT INTO theme (name, description, thumbnail)
VALUES ('테마3', '놀라움', '/image/default.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('10:00');
INSERT INTO reservation_time (start_at)
VALUES ('11:00');
INSERT INTO reservation_time (start_at)
VALUES ('12:00');

INSERT INTO member (name, email, password, role)
VALUES ('포라', 'forarium20@gmail.com', '1234', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('코기', 'ind07152@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('율무', 'ind07162@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('ADMIN', 'admin@naver.com', '1234', 'ADMIN');