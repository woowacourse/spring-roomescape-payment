SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE waiting RESTART IDENTITY;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO member(name, email, password, role)
VALUES ('테드', 'test1@email.com', '1450575459', 'USER'),
       ('아톰', 'test2@email.com', '1450575459', 'USER'),
       ('종이', 'test3@email.com', '1450575459', 'USER'),
       ('오리', 'test4@email.com', '1450575459', 'USER');

INSERT INTO theme (theme_name, description, thumbnail)
VALUES ('테마1', '테마1 설명 설명 설명', 'thumbnail1.jpg');

INSERT INTO reservation_time (start_at)
VALUES ('10:00');

INSERT INTO payment (order_id, payment_key, amount)
VALUES ('oderId1','paymentKey1', 1000),
       ('oderId2','paymentKey2', 1000),
       ('oderId3','paymentKey3', 1000),
       ('oderId4','paymentKey4', 1000),
       ('oderId5','paymentKey5', 1000),
       ('oderId6','paymentKey6', 1000),
       ('oderId7','paymentKey7', 1000),
       ('oderId8','paymentKey8', 1000),
       ('oderId9','paymentKey9', 1000),
       ('oderId10','paymentKey10', 1000),
       ('oderId11','paymentKey11', 1000),
       ('oderId12','paymentKey12', 1000),
       ('oderId13','paymentKey13', 1000),
       ('oderId14','paymentKey14', 1000),
       ('oderId15','paymentKey15', 1000),
       ('oderId16','paymentKey16', 1000),
       ('oderId17','paymentKey17', 1000),
       ('oderId18','paymentKey18', 1000);

INSERT INTO reservation (date, time_id, theme_id, member_id, status, payment_id)
VALUES (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 1, 'RESERVED', 1),
       (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 2, 'WAITING', 2),
       (DATEADD('DAY', 1, CURRENT_DATE), 1, 1, 3, 'WAITING', 3);

INSERT INTO waiting (reservation_id, waiting_order)
VALUES (2, 1),
       (3, 2);
