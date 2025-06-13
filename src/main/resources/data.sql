INSERT INTO theme(name, description, thumbnail)
VALUES ('테마1', '설명1', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마2', '설명2', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마3', '설명3', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마4', '설명4', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마5', '설명5', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마6', '설명6', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마7', '설명7', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마8', '설명8', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마9', '설명9', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마10', '설명10', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('테마11', '설명11', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation_time(start_at)
VALUES ('10:00'),
       ('11:00'),
       ('12:00');

INSERT INTO member(name, email, password, role)
VALUES ('어드민', 'admin@email.com', '1234', 'ADMIN'),
       ('유저', 'user@email.com', '1234', 'USER');

INSERT INTO reservation_slot(date, time_id, theme_id)
VALUES (CURRENT_DATE, 1, 1),
       (CURRENT_DATE, 1, 2),
       (CURRENT_DATE + 1, 3, 3),
       (CURRENT_DATE + 2, 1, 3);

INSERT INTO payment(amount, order_id, payment_key)
VALUES (1000, 1, '12345'),
       (1000, 2, '12345'),
       (1000, 2, '12345'),
       (1000, 2, '12345');

INSERT INTO reservation(reservation_slot_id, member_id, payment_id)

VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3),
       (4, 2, 4);

INSERT INTO waiting(member_id, reservation_slot_id, status)
VALUES (1, 4, 'APPROVE');
