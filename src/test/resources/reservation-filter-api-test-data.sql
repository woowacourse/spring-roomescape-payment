SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE waiting RESTART IDENTITY;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO member(name, email, password, role) VALUES
('테드', 'test1@email.com', '1450575459', 'USER'),
('종이', 'test1@email.com', '1450575459', 'USER');

INSERT INTO theme (theme_name, description, thumbnail) VALUES
('테마1', '테마1 설명 설명 설명', 'thumbnail1.jpg'),
('테마2', '테마2 설명 설명 설명', 'thumbnail2.jpg');

INSERT INTO reservation_time (start_at) VALUES
('10:00'),
('12:00');

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

INSERT INTO reservation (date, time_id, theme_id, member_id, status, payment_id) VALUES
('2024-05-01', 1, 1, 1, 'RESERVED', 1),
('2024-05-02', 1, 2, 1, 'RESERVED', 2),
('2024-05-03', 2, 1, 2, 'RESERVED', 3),
('2024-05-04', 2, 2, 2, 'RESERVED', 4);
