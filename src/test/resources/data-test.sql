SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE reservation RESTART IDENTITY;
TRUNCATE TABLE reservation_time RESTART IDENTITY;
TRUNCATE TABLE theme RESTART IDENTITY;
TRUNCATE TABLE member RESTART IDENTITY;
TRUNCATE TABLE payment RESTART IDENTITY;
TRUNCATE TABLE waiting RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO reservation_time (start_at)
VALUES ('15:40'),
       ('13:40'),
       ('17:40');

INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'lemone@gmail.com', 'lemone', 'ADMIN'),
       ('일반', 'sancho@gmail.com', 'sancho', 'MEMBER');

INSERT INTO theme (name, description, thumbnail, price)
VALUES ('polla', '폴라 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 10000),
       ('dobby', '도비 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 15000),
       ('sancho', '산초 방탈출', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 20000);

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id)
VALUES ('2024-04-30', 1, 1, 1),
       ('2024-04-30', 1, 1, 1),
       ('2024-05-01', 2, 1, 2),
       ('2024-05-02', 2, 2, 2),
       ('2024-05-03', 2, 2, 1),
       ('2024-05-04', 1, 1, 2);

INSERT INTO payment(external_payment_key, external_order_id, created_at, reservation_id)
VALUES
    ('externalPaymentKey1', 'externalOrderId1', '2024-04-29T10:00:00', 1),
    ('externalPaymentKey2', 'externalOrderId2', '2024-04-29T11:00:00', 2),
    ('externalPaymentKey3', 'externalOrderId3', '2024-04-30T12:00:00', 3),
    ('externalPaymentKey4', 'externalOrderId4', '2024-04-30T13:00:00', 4),
    ('externalPaymentKey5', 'externalOrderId5', '2024-05-01T14:00:00', 5);
