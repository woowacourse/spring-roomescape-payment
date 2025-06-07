INSERT INTO theme (name, description, thumbnail) VALUES
    ('테마1', '재밌음', '/image/default.jpg'),
    ('테마2', '무서움', '/image/default.jpg'),
    ('테마3', '놀라움', '/image/default.jpg');

INSERT INTO time_slot (start_at) VALUES
    ('10:00'),
    ('11:00'),
    ('12:00');

INSERT INTO member (name, email, password) VALUES
    ('유저1', 'member1@email.com', 'password'),
    ('유저2', 'member2@email.com', 'password'),
    ('유저3', 'member3@email.com', 'password');

INSERT INTO admin (name, email, password) VALUES
    ('어드민', 'admin@email.com', 'password');

INSERT INTO reservation (date, time_id, theme_id, member_id, status) VALUES
    (CURRENT_DATE + 1, 1, 1, 1, 'RESERVED'),
    (CURRENT_DATE + 2, 1, 1, 1, 'RESERVED'),
    (CURRENT_DATE + 1, 2, 3, 2, 'RESERVED'),
    (CURRENT_DATE + 3, 2, 2, 2, 'RESERVED'),
    (CURRENT_DATE + 2, 3, 2, 3, 'RESERVED'),
    (CURRENT_DATE + 3, 3, 2, 3, 'RESERVED');

INSERT INTO payment (payment_key, order_id, amount, approved_at, reservation_id) VALUES
    ('tgen_20240513184816ZSAZ9', 'MC4wNDYzMzA0OTc2MDgy', 1000,  CURRENT_TIMESTAMP + INTERVAL '1' HOUR, 1),
    ('tgen_20240513184816ZSAZ9', 'MC4wNDYzMzA0OTc2MDgy', 2000,  CURRENT_TIMESTAMP + INTERVAL '2' HOUR, 2),
    ('tgen_20240513184816ZSAZ9', 'MC4wNDYzMzA0OTc2MDgy', 3000,  CURRENT_TIMESTAMP + INTERVAL '3' HOUR, 3);

INSERT INTO waiting (reservation_id, member_id) VALUES
    (1, 2),
    (3, 3),
    (3, 1),
    (4, 1);
