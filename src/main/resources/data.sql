INSERT INTO member (name, email, password, role)
VALUES ('산초(어드민)', 'sancho@admin.com', 'sancho', 'ADMIN'),
       ('제이(어드민)', 'jay@admin.com', 'jay', 'ADMIN'),
       ('산초(일반)', 'sancho@member.com', 'sancho', 'MEMBER'),
       ('제이(일반)', 'jay@member.com', 'jay', 'MEMBER');

INSERT INTO reservation_time (start_at)
VALUES ('15:40'),
       ('13:40'),
       ('17:40');

INSERT INTO theme (name, description, thumbnail, price)
VALUES ('테마1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 10000),
       ('테마2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 15000),
       ('테마3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg', 20000);

INSERT INTO reservation (date, reservation_time_id, theme_id, member_id)
VALUES ('2024-06-05', 1, 2, 3),
       ('2024-06-06', 1, 1, 3),
       ('2024-06-07', 1, 2, 3),
       ('2024-06-07', 1, 1, 4),
       ('2024-06-07', 2, 1, 4),
       ('2024-06-05', 2, 3, 4);

INSERT INTO waiting (reservation_id, created_at)
VALUES (1, '2024-05-27T13:45:30'),
       (2, '2024-05-27T14:00:00'),
       (2, '2024-05-28T12:12:12');

INSERT INTO payment(external_payment_key, external_order_id, created_at, reservation_id)
VALUES
    ('externalPaymentKey1', 'externalOrderId1', '2024-04-29T10:00:00', 1),
    ('externalPaymentKey2', 'externalOrderId2', '2024-04-29T11:00:00', 2),
    ('externalPaymentKey3', 'externalOrderId3', '2024-04-30T12:00:00', 3),
    ('externalPaymentKey4', 'externalOrderId4', '2024-04-30T13:00:00', 4),
    ('externalPaymentKey5', 'externalOrderId5', '2024-05-01T14:00:00', 5),
    ('externalPaymentKey6', 'externalOrderId6', '2024-05-01T15:00:00', 6);