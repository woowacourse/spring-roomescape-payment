-- PaymentResult 테스트 데이터 먼저 삽입
INSERT INTO payment_result(order_id, payment_key, payment_type, amount, created_at, updated_at)
VALUES ('ORDER_20250510_001', 'payment_key_mc4yK4bW0D7', 'CARD', 25000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_002', 'payment_key_nE9xL8cX1F2', 'TRANSFER', 30000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_003', 'payment_key_oP2mN9dY3G4', 'CARD', 35000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_004', 'payment_key_qR5nO1eZ6H7', 'VIRTUAL_ACCOUNT', 40000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_005', 'payment_key_sT8pQ3fA9I0', 'CARD', 28000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_006', 'payment_key_uV1rS5gB2J3', 'TRANSFER', 32000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250510_007', 'payment_key_wX4tU7hC5K6', 'CARD', 45000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250530_008', 'payment_key_yZ7vW9iD8L9', 'VIRTUAL_ACCOUNT', 38000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281'),
       ('ORDER_20250530_009', 'payment_key_aB0xY2jE1M2', 'CARD', 42000, '2025-05-23 19:37:43.488281',
        '2025-05-23 19:37:43.488281');


-- member
INSERT INTO member(role, name, email, password, created_at, updated_at)
VALUES ('GENERAL', 'member1', 'member1@email.com', 'qwer1234!', '2025-05-23 17:37:43.488281',
        '2025-05-23 17:37:43.488281'),
       ('GENERAL', 'member2', 'member2@email.com', 'qwer1234!', '2025-05-23 17:37:43.488281',
        '2025-05-23 17:37:43.488281'),
       ('GENERAL', 'member3', 'member3@email.com', 'qwer1234!', '2025-05-23 17:37:43.488281',
        '2025-05-23 17:37:43.488281'),
       ('GENERAL', 'member4', 'member4@email.com', 'qwer1234!', '2025-05-23 17:37:43.488281',
        '2025-05-23 17:37:43.488281'),
       ('ADMIN', 'admin', 'admin@email.com', 'qwer1234!', '2025-05-23 17:37:43.488281', '2025-05-23 17:37:43.488281');

-- reservation_time
INSERT INTO reservation_time(start_at, created_at, updated_at)
VALUES ('10:00', '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('11:00', '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('12:00', '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('13:00', '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281');

-- theme
INSERT INTO theme(name, description, thumbnail, created_at, updated_at)
VALUES ('Theme 1', '설명1',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQqlkX2ISwyii-yHkQmp-Ad0hsfekERx2RNEa_RFNrr25BDWEAxHRgghcPid7ckxbLngE&usqp=CAU',
        '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('Theme 2', '설명2',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQqlkX2ISwyii-yHkQmp-Ad0hsfekERx2RNEa_RFNrr25BDWEAxHRgghcPid7ckxbLngE&usqp=CAU',
        '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('Theme 3', '설명3',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQqlkX2ISwyii-yHkQmp-Ad0hsfekERx2RNEa_RFNrr25BDWEAxHRgghcPid7ckxbLngE&usqp=CAU',
        '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281'),
       ('Theme 4', '설명4',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQqlkX2ISwyii-yHkQmp-Ad0hsfekERx2RNEa_RFNrr25BDWEAxHRgghcPid7ckxbLngE&usqp=CAU',
        '2025-05-23 18:37:43.488281', '2025-05-23 18:37:43.488281');

-- reservation
INSERT INTO reservation(date, reservation_time_id, theme_id, member_id, created_at, updated_at, payment_result_id)
VALUES ('2025-05-10', 1, 1, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 1),
       ('2025-05-10', 1, 2, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 2),
       ('2025-05-10', 2, 2, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 3),
       ('2025-05-10', 1, 3, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 4),
       ('2025-05-10', 2, 3, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 5),
       ('2025-05-10', 3, 3, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 6),
       ('2025-05-10', 1, 4, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 7),
       ('2025-05-30', 3, 4, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 8),
       ('2025-05-30', 4, 4, 1, '2025-05-23 19:37:43.488281', '2025-05-23 19:37:43.488281', 9);

-- waiting
INSERT INTO waiting(date, theme_id, time_id, member_id, created_at, updated_at, payment_result_id)
VALUES ('2025-05-10', 1, 1, 2, '2025-05-23 20:37:43.488281', '2025-05-23 20:37:43.488281', NULL),
       ('2025-05-10', 1, 1, 3, '2025-05-23 21:37:43.488281', '2025-05-23 20:37:43.488281', NULL),
       ('2025-05-10', 1, 1, 4, '2025-05-23 22:37:43.488281', '2025-05-23 20:37:43.488281', NULL);

