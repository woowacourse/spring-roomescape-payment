INSERT INTO theme (name, description, thumbnail) VALUES
    ('테마1', '재밌음', '/image/default.jpg'),
    ('테마2', '무서움', '/image/default.jpg'),
    ('테마3', '놀라움', '/image/default.jpg');

INSERT INTO reservation_time (start_at) VALUES
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
    (CURRENT_DATE + 1, 2, 1, 1, 'RESERVED'),
    (CURRENT_DATE - 1, 1, 3, 2, 'RESERVED'),
    (CURRENT_DATE + 2, 1, 2, 3, 'RESERVED');

INSERT INTO waiting (reservation_id, member_id) VALUES
    (1, 2),
    (1, 3);
