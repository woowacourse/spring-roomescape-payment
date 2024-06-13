INSERT INTO member (name, email, password, role)
VALUES ('테니', 'tenny@email.com', '1234', 'ADMIN');
INSERT INTO member (name, email, password, role)
VALUES ('냥인', 'nyangin@email.com', '1234', 'MEMBER');

INSERT INTO reservation_time (start_at)
VALUES ('18:00'),
       ('19:00');

INSERT INTO theme (name, description, thumbnail)
VALUES ('포켓몬', '매우 귀엽습니다.', 'https://i.pinimg.com/474x/b3/aa/d7/b3aad752a5fbda932dd37015bca3047f.jpg'),
       ('커비', '매우 재밌습니다.', 'https://i.pinimg.com/474x/e2/55/4d/e2554dea5499f88c242178ce7ed568d9.jpg'),
       ('호러3', '매우 무섭습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('호러4', '매우 무섭습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('호러5', '매우 무섭습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('추리2', '매우 어렵습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('추리3', '매우 어렵습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('추리4', '매우 어렵습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('추리5', '매우 어렵습니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg'),
       ('준비 중', '준비 중입니다.', 'https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg');

INSERT INTO reservation (member_id, date, time_id, theme_id, payment_key, amount)
VALUES (2, '2034-05-08', 1, 2, 'success', 1000),
       (2, '2034-05-08', 1, 1, 'success', 1000),
       (2, '2034-05-09', 2, 3, 'success', 1000),
       (2, '2034-05-09', 1, 4, 'success', 1000),
       (2, '2034-05-09', 2, 5, 'success', 1000),
       (2, '2034-05-09', 2, 6, 'success', 1000),
       (2, '2034-05-10', 2, 7, 'success', 1000),
       (2, '2034-05-10', 2, 8, 'success', 1000),
       (2, '2034-05-11', 2, 9, 'success', 1000),
       (2, '2034-05-11', 2, 10, 'success', 1000),
       (2, '2024-06-08', 2, 10, 'success', 1000),
       (1, '2024-05-22', 2, 10, 'success', 1000);

INSERT INTO reservation (member_id, date, time_id, theme_id, amount)
VALUES (1, '2024-06-22', 2, 10, 1000);

INSERT INTO waiting (member_id, date, time_id, theme_id)
VALUES (1, '2024-05-22', 1, 2);
