SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE waiting RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO reservation_time (start_at)
VALUES ('15:40'),
       ('13:40'),
       ('17:40');

INSERT INTO member (name, email, password, role)
VALUES ('레모네(어드민)', 'lemone@gmail.com', 'lemone123', 'ADMIN'),
       ('산초(일반)', 'sancho@gmail.com', 'sancho123', 'MEMBER');

INSERT INTO theme (name, description, thumbnail)
VALUES ('polla', '폴라 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('dobby', '도비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('sancho', '산초 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id)
VALUES ('2024-04-30', 1, 1, 1),
       ('2024-04-30', 1, 1, 1),
       ('2024-05-01', 2, 1, 2),
       ('2024-05-02', 2, 2, 2),
       ('2024-05-03', 2, 2, 1);

