insert into member (name, email, password, role)
values ('찰리', 'gomding@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('비토', 'bito@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN');

insert into reservation_time (start_at)
values ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00');

insert into theme (name, description, thumbnail, price)
values ('테마1', '테마1 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000),
       ('테마2', '테마2 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000),
       ('테마3', '테마3 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000),
       ('테마4', '테마4 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000),
       ('테마5', '테마5 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000),
       ('테마6', '테마6 설명', 'https://upload.wikimedia.org/wikipedia/en/thumb/3/3b/SpongeBob_SquarePants_character.svg/440px-SpongeBob_SquarePants_character.svg.png', 1000);

insert into reservation (member_id, date, time_id, theme_id, status)
values (1, DATEADD(DAY, 1, CURRENT_DATE), 1, 2, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 1, 4, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 1, 1, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 1, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 2, 2, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 3, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, 1, CURRENT_DATE), 2, 4, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 1, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, 1, CURRENT_DATE), 3, 2, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 3, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, 1, CURRENT_DATE), 3, 4, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 4, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 5, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, 1, CURRENT_DATE), 4, 1, 'PAYMENT_WAITING');

insert into reservation (member_id, date, time_id, theme_id, status)
values (1, DATEADD(DAY, -1, CURRENT_DATE), 1, 2, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 1, 3, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 1, 1, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 1, 4, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 1, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 2, 2, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 3, 'PAYMENT_WAITING'),
       (4, DATEADD(DAY, -1, CURRENT_DATE), 2, 4, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 3, 1, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 3, 2, 'PAYMENT_WAITING'),
       (3, DATEADD(DAY, -1, CURRENT_DATE), 3, 3, 'PAYMENT_WAITING'),
       (1, DATEADD(DAY, -1, CURRENT_DATE), 3, 4, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 4, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 5, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -1, CURRENT_DATE), 4, 2, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -2, CURRENT_DATE), 3, 2, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -3, CURRENT_DATE), 3, 3, 'PAYMENT_WAITING'),
       (2, DATEADD(DAY, -4, CURRENT_DATE), 3, 1, 'PAYMENT_WAITING');
