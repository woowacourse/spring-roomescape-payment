-- 테마 데이터
INSERT INTO themes(name, description, thumbnail)
VALUES ('추리', '셜록 with Danny', 'image/thumbnail.png'),
       ('공포', '어둠 속의 비명', 'image/thumbnail.png'),
       ('모험', '잃어버린 도시', 'image/thumbnail.png'),
       ('SF', '우주 탈출 미션', 'image/thumbnail.png'),
       ('감성', '시간을 걷는 집', 'image/thumbnail.png'),
       ('판타지', '마법사의 유산', 'image/thumbnail.png'),
       ('역사', '고려 왕실의 비밀', 'image/thumbnail.png'),
       ('범죄', '은행 강도 사건', 'image/thumbnail.png'),
       ('스릴러', '잠입 작전', 'image/thumbnail.png'),
       ('코미디', '웃음 연구소', 'image/thumbnail.png'),
       ('로맨스', '잃어버린 편지', 'image/thumbnail.png'),
       ('논리', '퍼즐 마스터', 'image/thumbnail.png');

-- 예약 시간 데이터
INSERT INTO reservation_times(start_at)
VALUES ('08:00'),
       ('12:00'),
       ('14:00'),
       ('16:00'),
       ('18:00');

-- 회원 데이터
INSERT INTO members (name, email, password, member_role)
VALUES ('Admin', 'admin@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'ADMIN'),
       ('Regular', 'user@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Alice', 'alice@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Bob', 'bob@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Carol', 'carol@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Dave', 'dave@example.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Eve', 'eve@example.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
       ('Frank', 'frank@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Grace', 'grace@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Heidi', 'heidi@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Ivan', 'ivan@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Judy', 'judy@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Mallory', 'mallory@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Niaj', 'niaj@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Olivia', 'olivia@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Peggy', 'peggy@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Rupert', 'rupert@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Sybil', 'sybil@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Trent', 'trent@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Uma', 'uma@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Victor', 'victor@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Wendy', 'wendy@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Xander', 'xander@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Yvonne', 'yvonne@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Zack', 'zack@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Amy', 'amy@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Brian', 'brian@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Chloe', 'chloe@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR'),
       ('Daniel', 'daniel@example.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqF.W', 'REGULAR');

-- 예약 슬롯 데이터 (ReservationSlot) - 각 테마별 고유한 날짜/시간 조합
INSERT INTO reservation_slots(date, time_id, theme_id)
VALUES
    -- theme_id = 12 (논리) - 2025-05-10 (5개 슬롯)
    ('2025-05-10', 1, 12), -- ID: 1 (08:00)
    ('2025-05-10', 2, 12), -- ID: 2 (12:00)
    ('2025-05-10', 3, 12), -- ID: 3 (14:00)
    ('2025-05-10', 4, 12), -- ID: 4 (16:00)
    ('2025-05-10', 5, 12), -- ID: 5 (18:00)

    -- theme_id = 11 (로맨스) - 2025-05-10 (5개 슬롯)
    ('2025-05-10', 1, 11), -- ID: 6 (08:00)
    ('2025-05-10', 2, 11), -- ID: 7 (12:00)
    ('2025-05-10', 3, 11), -- ID: 8 (14:00)
    ('2025-05-10', 4, 11), -- ID: 9 (16:00)
    ('2025-05-10', 5, 11), -- ID: 10 (18:00)

    -- theme_id = 3 (모험) - 2025-05-10 (4개 슬롯)
    ('2025-05-10', 1, 3),  -- ID: 11 (08:00)
    ('2025-05-10', 2, 3),  -- ID: 12 (12:00)
    ('2025-05-10', 3, 3),  -- ID: 13 (14:00)
    ('2025-05-10', 4, 3),  -- ID: 14 (16:00)

    -- theme_id = 4 (SF) - 2025-05-11 (3개 슬롯)
    ('2025-05-11', 1, 4),  -- ID: 15 (08:00)
    ('2025-05-11', 2, 4),  -- ID: 16 (12:00)
    ('2025-05-11', 3, 4),  -- ID: 17 (14:00)

    -- theme_id = 5 (감성) - 2025-05-11 (2개 슬롯)
    ('2025-05-11', 1, 5),  -- ID: 18 (08:00)
    ('2025-05-11', 2, 5);  -- ID: 19 (12:00)

-- 예약 데이터 (Reservation) - 수정된 ReservationSlot ID에 맞게 조정
INSERT INTO reservations(reservation_slot_id, member_id, created_at)
VALUES
    -- ReservationSlot ID 1 (논리, 5/10, 08:00) - Alice(현재), Bob(대기), Carol(대기)
    (1, 3, '2025-05-09 10:00:00'),   -- Alice
    (1, 4, '2025-05-09 10:30:00'),   -- Bob
    (1, 5, '2025-05-09 11:00:00'),   -- Carol

    -- ReservationSlot ID 2 (논리, 5/10, 12:00) - Dave(현재), Eve(대기)
    (2, 6, '2025-05-09 11:30:00'),   -- Dave
    (2, 7, '2025-05-09 12:00:00'),   -- Eve

    -- ReservationSlot ID 3 (논리, 5/10, 14:00) - Frank(현재)
    (3, 8, '2025-05-09 12:30:00'),   -- Frank

    -- ReservationSlot ID 4 (논리, 5/10, 16:00) - Grace(현재), Heidi(대기)
    (4, 9, '2025-05-09 13:00:00'),   -- Grace
    (4, 10, '2025-05-09 13:30:00'),  -- Heidi

    -- ReservationSlot ID 5 (논리, 5/10, 18:00) - Ivan(현재), Judy(대기), Mallory(대기)
    (5, 11, '2025-05-09 14:00:00'),  -- Ivan
    (5, 12, '2025-05-09 14:30:00'),  -- Judy
    (5, 13, '2025-05-09 15:00:00'),  -- Mallory

    -- ReservationSlot ID 6 (로맨스, 5/10, 08:00) - Niaj(현재), Olivia(대기)
    (6, 14, '2025-05-09 15:30:00'),  -- Niaj
    (6, 15, '2025-05-09 16:00:00'),  -- Olivia

    -- ReservationSlot ID 7 (로맨스, 5/10, 12:00) - Peggy(현재)
    (7, 16, '2025-05-09 16:30:00'),  -- Peggy

    -- ReservationSlot ID 8 (로맨스, 5/10, 14:00) - Rupert(현재), Sybil(대기)
    (8, 17, '2025-05-09 17:00:00'),  -- Rupert
    (8, 18, '2025-05-09 17:30:00'),  -- Sybil

    -- ReservationSlot ID 9 (로맨스, 5/10, 16:00) - Trent(현재), Uma(대기)
    (9, 19, '2025-05-09 18:00:00'),  -- Trent
    (9, 20, '2025-05-09 18:30:00'),  -- Uma

    -- ReservationSlot ID 10 (로맨스, 5/10, 18:00) - Victor(현재)
    (10, 21, '2025-05-09 19:00:00'), -- Victor

    -- ReservationSlot ID 11 (모험, 5/10, 08:00) - Wendy(현재), Xander(대기)
    (11, 22, '2025-05-09 19:30:00'), -- Wendy
    (11, 23, '2025-05-09 20:00:00'), -- Xander

    -- ReservationSlot ID 12 (모험, 5/10, 12:00) - Yvonne(현재), Zack(대기)
    (12, 24, '2025-05-09 20:30:00'), -- Yvonne
    (12, 25, '2025-05-09 21:00:00'), -- Zack

    -- ReservationSlot ID 13 (모험, 5/10, 14:00) - Amy(현재)
    (13, 26, '2025-05-09 21:30:00'), -- Amy

    -- ReservationSlot ID 14 (모험, 5/10, 16:00) - Brian(현재), Chloe(대기)
    (14, 27, '2025-05-09 22:00:00'), -- Brian
    (14, 28, '2025-05-09 22:30:00'), -- Chloe

    -- 5월 11일 예약들
    -- ReservationSlot ID 15 (SF, 5/11, 08:00) - Daniel(현재), Alice(대기)
    (15, 29, '2025-05-10 10:00:00'), -- Daniel
    (15, 3, '2025-05-10 10:30:00'),  -- Alice (다른 예약)

    -- ReservationSlot ID 16 (SF, 5/11, 12:00) - Bob(현재), Carol(대기)
    (16, 4, '2025-05-10 11:00:00'),  -- Bob
    (16, 5, '2025-05-10 11:30:00'),  -- Carol

    -- ReservationSlot ID 17 (SF, 5/11, 14:00) - Dave(현재), Eve(대기), Frank(대기)
    (17, 6, '2025-05-10 12:00:00'),  -- Dave
    (17, 7, '2025-05-10 12:30:00'),  -- Eve
    (17, 8, '2025-05-10 13:00:00'),  -- Frank

    -- ReservationSlot ID 18 (감성, 5/11, 08:00) - Grace(현재)
    (18, 9, '2025-05-10 13:30:00'),  -- Grace

    -- ReservationSlot ID 19 (감성, 5/11, 12:00) - Heidi(현재), Ivan(대기)
    (19, 10, '2025-05-10 14:00:00'), -- Heidi
    (19, 11, '2025-05-10 14:30:00'); -- Ivan

INSERT INTO payments(payment_key, order_id, amount, product_type, product_id, created_at)
VALUES
    ('tgen_20250519100000Secret001', 'RESERVATION_ORDER_MC410Secret001', '1000', 'RESERVATION', 1, '2025-05-09 10:00:00'),
    ('tgen_20250519103000Secret002', 'RESERVATION_ORDER_MC410Secret002', '1000', 'RESERVATION', 2, '2025-05-09 10:30:00'),
    ('tgen_20250509110000Secret003', 'RESERVATION_ORDER_MC410Secret003', '1000', 'RESERVATION', 3, '2025-05-09 11:00:00'),
    ('tgen_20250509113000Secret004', 'RESERVATION_ORDER_MC410Secret004', '1000', 'RESERVATION', 4, '2025-05-09 11:30:00'),
    ('tgen_20250509120000Secret005', 'RESERVATION_ORDER_MC410Secret005', '1000', 'RESERVATION', 5, '2025-05-09 12:00:00'),
    ('tgen_20250509123000Secret006', 'RESERVATION_ORDER_MC410Secret006', '1000', 'RESERVATION', 6, '2025-05-09 12:30:00'),
    ('tgen_20250509130000Secret007', 'RESERVATION_ORDER_MC410Secret007', '1000', 'RESERVATION', 7, '2025-05-09 13:00:00'),
    ('tgen_20250509133000Secret008', 'RESERVATION_ORDER_MC410Secret008', '1000', 'RESERVATION', 8, '2025-05-09 13:30:00'),
    ('tgen_20250509140000Secret009', 'RESERVATION_ORDER_MC410Secret009', '1000', 'RESERVATION', 9, '2025-05-09 14:00:00'),
    ('tgen_20250509143000Secret010', 'RESERVATION_ORDER_MC410Secret010', '1000', 'RESERVATION', 10, '2025-05-09 14:30:00'),
    ('tgen_20250509150000Secret011', 'RESERVATION_ORDER_MC410Secret011', '1000', 'RESERVATION', 11, '2025-05-09 15:00:00'),
    ('tgen_20250509153000Secret012', 'RESERVATION_ORDER_MC410Secret012', '1000', 'RESERVATION', 12, '2025-05-09 15:30:00'),
    ('tgen_20250509160000Secret013', 'RESERVATION_ORDER_MC410Secret013', '1000', 'RESERVATION', 13, '2025-05-09 16:00:00'),
    ('tgen_20250509163000Secret014', 'RESERVATION_ORDER_MC410Secret014', '1000', 'RESERVATION', 14, '2025-05-09 16:30:00'),
    ('tgen_20250509170000Secret015', 'RESERVATION_ORDER_MC410Secret015', '1000', 'RESERVATION', 15, '2025-05-09 17:00:00'),
    ('tgen_20250509173000Secret016', 'RESERVATION_ORDER_MC410Secret016', '1000', 'RESERVATION', 16, '2025-05-09 17:30:00'),
    ('tgen_20250509180000Secret017', 'RESERVATION_ORDER_MC410Secret017', '1000', 'RESERVATION', 17, '2025-05-09 18:00:00'),
    ('tgen_20250509183000Secret018', 'RESERVATION_ORDER_MC410Secret018', '1000', 'RESERVATION', 18, '2025-05-09 18:30:00'),
    ('tgen_20250509190000Secret019', 'RESERVATION_ORDER_MC410Secret019', '1000', 'RESERVATION', 19, '2025-05-09 19:00:00'),
    ('tgen_20250509193000Secret020', 'RESERVATION_ORDER_MC410Secret020', '1000', 'RESERVATION', 20, '2025-05-09 19:30:00'),
    ('tgen_20250509200000Secret021', 'RESERVATION_ORDER_MC410Secret021', '1000', 'RESERVATION', 21, '2025-05-09 20:00:00'),
    ('tgen_20250509203000Secret022', 'RESERVATION_ORDER_MC410Secret022', '1000', 'RESERVATION', 22, '2025-05-09 20:30:00'),
    ('tgen_20250509210000Secret023', 'RESERVATION_ORDER_MC410Secret023', '1000', 'RESERVATION', 23, '2025-05-09 21:00:00'),
    ('tgen_20250509213000Secret024', 'RESERVATION_ORDER_MC410Secret024', '1000', 'RESERVATION', 24, '2025-05-09 21:30:00'),
    ('tgen_20250509220000Secret025', 'RESERVATION_ORDER_MC410Secret025', '1000', 'RESERVATION', 25, '2025-05-09 22:00:00'),
    ('tgen_20250509223000Secret026', 'RESERVATION_ORDER_MC410Secret026', '1000', 'RESERVATION', 26, '2025-05-09 22:30:00'),
    ('tgen_20250510100000Secret027', 'RESERVATION_ORDER_MC410Secret027', '1000', 'RESERVATION', 27, '2025-05-10 10:00:00'),
    ('tgen_20250510103000Secret028', 'RESERVATION_ORDER_MC410Secret028', '1000', 'RESERVATION', 28, '2025-05-10 10:30:00'),
    ('tgen_20250510110000Secret029', 'RESERVATION_ORDER_MC410Secret029', '1000', 'RESERVATION', 29, '2025-05-10 11:00:00'),
    ('tgen_20250510113000Secret030', 'RESERVATION_ORDER_MC410Secret030', '1000', 'RESERVATION', 30, '2025-05-10 11:30:00'),
    ('tgen_20250510120000Secret031', 'RESERVATION_ORDER_MC410Secret031', '1000', 'RESERVATION', 31, '2025-05-10 12:00:00'),
    ('tgen_20250510123000Secret032', 'RESERVATION_ORDER_MC410Secret032', '1000', 'RESERVATION', 32, '2025-05-10 12:30:00'),
    ('tgen_20250510130000Secret033', 'RESERVATION_ORDER_MC410Secret033', '1000', 'RESERVATION', 33, '2025-05-10 13:00:00'),
    ('tgen_20250510133000Secret034', 'RESERVATION_ORDER_MC410Secret034', '1000', 'RESERVATION', 34, '2025-05-10 13:30:00'),
    ('tgen_20250510140000Secret035', 'RESERVATION_ORDER_MC410Secret035', '1000', 'RESERVATION', 35, '2025-05-10 14:00:00')
