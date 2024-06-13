INSERT INTO theme(name, description, thumbnail)
VALUES ('테마1', '테마1입니당 ^0^', 'https://file.miricanvas.com/template_thumb/2021/07/02/13/20/k4t92g5ntu46etia/thumb.jpg'),
       ('테마2', '테마2입니당 ^0^', 'https://file.miricanvas.com/template_thumb/2021/07/02/13/20/k4t92g5ntu46etia/thumb.jpg'),
       ('테마3', '테마3입니당 ^0^', 'https://file.miricanvas.com/template_thumb/2021/07/02/13/20/k4t92g5ntu46etia/thumb.jpg');

INSERT INTO reservation_time(start_at)
VALUES ('15:00:00'),
       ('16:00:00'),
       ('17:00:00');

INSERT INTO member(name, email, password, role)
VALUES ('스티치', 'lxxjn0@test.com', '123', 'MEMBER'),
       ('파랑', 'parang@test.com', '123', 'MEMBER'),
       ('마크', 'mark@test.com', '123', 'MEMBER'),
       ('메이슨', 'mason@test.com', '123', 'MEMBER'),
       ('어드민', 'admin@test.com', '123', 'ADMIN');


INSERT INTO reservation(member_id, date, time_id, theme_id, status, created_at)
VALUES (1, TIMESTAMPADD(DAY, 1, NOW()), 1, 1, 'RESERVED', TIMESTAMPADD(DAY, 1, NOW())),
       (2, TIMESTAMPADD(DAY, 1, NOW()), 2, 1, 'RESERVED', TIMESTAMPADD(DAY, 1, NOW())),
       (1, TIMESTAMPADD(DAY, 1, NOW()), 3, 1, 'RESERVED', TIMESTAMPADD(DAY, 1, NOW())),
       (3, TIMESTAMPADD(DAY, 1, NOW()), 1, 2, 'RESERVED', TIMESTAMPADD(DAY, 1, NOW())),
       (2, TIMESTAMPADD(DAY, 1, NOW()), 2, 2, 'RESERVED', TIMESTAMPADD(DAY, 1, NOW()));

INSERT INTO payment(reservation_id, payment_key, order_id, order_name, total_amount, requested_at)
VALUES (1, 'payment_key1', 'order_id1', '테마1 결제', 10000, TIMESTAMPADD(DAY, -3, NOW())),
       (2, 'payment_key2', 'order_id2', '테마1 결제', 10000, TIMESTAMPADD(DAY, -3, NOW())),
       (3, 'payment_key3', 'order_id3', '테마1 결제', 10000, TIMESTAMPADD(DAY, -3, NOW())),
       (4, 'payment_key4', 'order_id4', '테마2 결제', 10000, TIMESTAMPADD(DAY, -3, NOW())),
       (5, 'payment_key5', 'order_id5', '테마2 결제', 10000, TIMESTAMPADD(DAY, -3, NOW()));
