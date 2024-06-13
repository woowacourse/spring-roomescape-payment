insert into member (name, email, password, role)
values ('썬', 'sun@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('비토', 'bito@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN');

insert into reservation_time (start_at)
values ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00');

insert into theme (name, description, thumbnail, price)
values ('테마1', '테마1 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 10000),
       ('테마2', '테마2 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 20000),
       ('테마3', '테마3 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 30000),
       ('테마4', '테마4 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 40000),
       ('테마5', '테마5 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 50000),
       ('테마6', '테마6 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 60000);

insert into reservation (member_id, date, time_id, theme_id, status)
values (1, DATEADD(DAY, 1, CURRENT_DATE), 1, 2, 'RESERVED'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 1, 4, 'RESERVED'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 1, 1, 'RESERVED'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 1, 'RESERVED'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 2, 2, 'RESERVED'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 3, 'RESERVED'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 4, 'RESERVED'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 1, 'RESERVED'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 3, 2, 'RESERVED'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 3, 'RESERVED'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 4, 'RESERVED'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 4, 'RESERVED'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 5, 'RESERVED'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 1, 2, 'WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 1, 4, 'WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 1, 3, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 6, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 1, 'RESERVED');

insert into reservation (member_id, date, time_id, theme_id, status)
values (1, DATEADD(DAY, -1, CURRENT_DATE), 1, 2, 'RESERVED'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 1, 3, 'RESERVED'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 1, 1, 'RESERVED'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 1, 4, 'RESERVED'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 1, 'RESERVED'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 2, 2, 'RESERVED'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 3, 'RESERVED'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 4, 'RESERVED'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 3, 1, 'RESERVED'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 3, 2, 'RESERVED'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 3, 3, 'RESERVED'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 3, 4, 'RESERVED'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 4, 'RESERVED'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 5, 'RESERVED'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 2, 'RESERVED'),
       (2, DATEADD(DAY, -2, CURRENT_DATE), 3, 2, 'RESERVED'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 3, 'RESERVED'),
       (2, DATEADD(DAY, -4, CURRENT_DATE), 3, 1, 'RESERVED');
