insert into member (id, email, name, password, role)
values (1, 'test@test.com', 'admin1', '12341234', 'ADMIN'),
       (2, 'test2@test.com', 'member2', '12341234', 'MEMBER'),
       (3, 'test3@test.com', 'member3', '12341234', 'MEMBER');

insert into theme (id, name, description, price, thumbnail_url)
values (1, '테마1', '테마1 설명', 10000, 'https://cdn3.emoji.gg/emojis/31249-blobfire.png'),
       (2, '테마2', '테마2 설명', 20000, 'https://cdn3.emoji.gg/emojis/25840-blobcookie.png'),
       (3, '테마3', '테마3 설명', 15000, 'https://cdn3.emoji.gg/emojis/24651-blobcat-sweat.png'),
       (4, '테마4', '테마4 설명', 30000, 'https://cdn3.emoji.gg/emojis/7629-blobooh.png'),
       (5, '테마5', '테마5 설명', 50000, 'https://cdn3.emoji.gg/emojis/4372-blobcuddle.png');

insert into reservation_time (id, start_at)
values (1, '12:00'),
       (2, '13:00'),
       (3, '14:00'),
       (4, '15:00'),
       (5, '16:00');

insert into reservation (id, member_id, date, theme_id, time_id, created_at, status)
values (1, 1, TIMESTAMPADD(DAY, -3, CURRENT_DATE), 1, 1, '2024-01-01', 'BOOKED'),
       (2, 2, TIMESTAMPADD(DAY, -1, CURRENT_DATE), 2, 2, '2024-01-01', 'BOOKED'),
       (3, 2, TIMESTAMPADD(DAY, -3, CURRENT_DATE), 1, 1, '2024-01-02', 'WAITING');

insert into reservation_payment (order_id, reservation_id, payment_key,amount)
values ('order_id1',1,'payment_key1',10000),
       ('order_id2',2,'payment_key2',20000);


alter table member alter column id restart with 1000;
alter table theme alter column id restart with 1000;
alter table reservation_time alter column id restart with 1000;
alter table reservation alter column id restart with 1000;
