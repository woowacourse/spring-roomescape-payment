SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE reservation;
ALTER TABLE  reservation ALTER COLUMN id RESTART;
TRUNCATE TABLE reservation_time;
ALTER TABLE  reservation_time ALTER COLUMN id RESTART;
TRUNCATE TABLE member;
ALTER TABLE  member ALTER COLUMN id RESTART;
TRUNCATE TABLE theme;
ALTER TABLE  theme ALTER COLUMN id RESTART;
SET REFERENTIAL_INTEGRITY TRUE;

insert into theme (name, description, thumbnail, price)
values  ('name1',
         'description1',
         'thumbnail1',
         1000),
        ('name2',
         'description2',
         'thumbnail2',
         1000),
        ('name3',
         'description3',
         'thumbnail3',
         1000),
        ('name4',
         'description4',
         'thumbnail4',
         1000),
        ('name5',
         'description5',
         'thumbnail5',
         1000),
        ('name6',
         'description6',
         'thumbnail6',
         1000),
        ('name7',
         'description7',
         'thumbnail7',
         1000),
        ('name8',
         'description8',
         'thumbnail8',
         1000),
        ('name9',
         'description9',
         'thumbnail9',
         1000),
        ('name10',
         'description10',
         'thumbnail10',
         1000),
        ('name11',
         'description11',
         'thumbnail11',
         1000),
        ('name12',
         'description12',
         'thumbnail12',
         1000),
        ('name13',
         'description13',
         'thumbnail13',
         1000);

insert into reservation_time (start_at)
values ('10:00'),
       ('11:00'),
       ('12:00'),
       ('13:00'),
       ('14:00'),
       ('15:00'),
       ('16:00'),
       ('17:00'),
       ('18:00'),
       ('19:00'),
       ('20:00'),
       ('21:00'),
       ('22:00');

insert into member (name, email, password, role)
values ('썬', 'sun@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('비토', 'bito@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC'),
       ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN');

insert into reservation (date, member_id, time_id, theme_id, status)
values (DATEADD(DAY, -5, CURRENT_DATE), 1, 1, 1, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 1, 2, 1, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 1, 3, 1, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 1, 4, 1, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 1, 1, 2, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 1, 2, 2, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 3, 2, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 1, 3, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 2, 3, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 1, 4, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 1, 5, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 2, 1, 6, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 3, 1, 7, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 3, 1, 8, 'RESERVED'),
       (DATEADD(DAY, -5, CURRENT_DATE), 3, 1, 9, 'RESERVED');
