INSERT INTO reservation_time(start_at)
VALUES ('10:00'),
       ('19:00');

INSERT INTO theme(name, description, thumbnail)
VALUES ('레벨2 탈출', '우테코 레벨2 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨3 탈출', '우테코 레벨3 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('레벨4 탈출', '우테코 레벨4 탈출기!', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO member(name, email, role, password)
VALUES ('관리자', 'admin@abc.com', 'ADMIN', '1234'),
       ('브리', 'bri@abc.com', 'USER', '1234'),
       ('브라운', 'brown@abc.com', 'USER', '1234'),
       ('오리', 'duck@abc.com', 'USER', '1234');
