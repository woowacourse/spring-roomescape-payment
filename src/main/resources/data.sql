INSERT INTO reservation_time (start_at)
VALUES ('15:40'),
       ('13:40'),
       ('17:40');

INSERT INTO member (name, email, password, role)
VALUES ('폴라(어드민)', 'polla@gmail.com', 'pollari99', 'ADMIN'),
       ('레모네(어드민)', 'lemone@wooteco.com', 'lemone1234', 'ADMIN'),
       ('산초(어드민)', 'sancho@wooteco.com', 'sancho', 'ADMIN'),
       ('제이(일반)', 'jay@wooteco.com', 'jay', 'MEMBER'),
       ('웨지(일반)', 'wedge@wooteco.com', 'wedge', 'MEMBER');

INSERT INTO theme (name, description, thumbnail, price)
VALUES ('테마 - polla', '폴라 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 10000),
       ('테마 - dobby', '도비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 15000),
       ('테마 - pobi', '포비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 20000);

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id)
VALUES ('2024-04-30', 1, 1, 1),
       ('2024-05-01', 2, 2, 2);

INSERT INTO waiting (reservation_id, member_id, created_at)
VALUES (1, 2, '2024-05-27T13:45:30'),
       (2, 1, '2024-05-27T14:00:00'),
       (2, 3, '2024-05-28T12:12:12');
