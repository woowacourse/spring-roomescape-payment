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
       ('Frank', 'frank@gmail.com', '$2a$10$lsczSamG1eaxq1KE2ivIpek7hOx.uNkDILI5nQPqaWyiUQtay6Msa', 'REGULAR'),
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


INSERT INTO payments(payment_key, order_id, amount, payment_type, payment_status)
VALUES
    -- 논리 테마 결제들 (30,000원)
    ('tgen_20250509_100000_001', 'ORDER_2025050910001', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 1 (Alice)
    ('tgen_20250509_113000_002', 'ORDER_2025050911302', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 2 (Dave)
    ('tgen_20250509_130000_003', 'ORDER_2025050913003', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 3 (Grace)
    ('tgen_20250509_140000_004', 'ORDER_2025050914004', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 4 (Ivan)

    -- 로맨스 테마 결제들 (35,000원)
    ('tgen_20250509_153000_005', 'ORDER_2025050915305', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 5 (Niaj)
    ('tgen_20250509_163000_006', 'ORDER_2025050916306', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 6 (Peggy)
    ('tgen_20250509_170000_007', 'ORDER_2025050917007', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 7 (Rupert)
    ('tgen_20250509_180000_008', 'ORDER_2025050918008', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 8 (Trent)
    ('tgen_20250509_190000_009', 'ORDER_2025050919009', 1000, 'NORMAL', 'APPROVED'),  -- Payment ID: 9 (Victor)

    -- 모험 테마 결제들 (40,000원)
    ('tgen_20250509_193000_010', 'ORDER_20250509193010', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 10 (Wendy)
    ('tgen_20250509_203000_011', 'ORDER_20250509203011', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 11 (Yvonne)
    ('tgen_20250509_213000_012', 'ORDER_20250509213012', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 12 (Amy)
    ('tgen_20250509_220000_013', 'ORDER_20250509220013', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 13 (Brian)

    -- SF 테마 결제들 (45,000원) - 5월 11일
    ('tgen_20250510_100000_014', 'ORDER_20250510100014', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 14 (Daniel)
    ('tgen_20250510_110000_015', 'ORDER_20250510110015', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 15 (Bob - 다른 예약)
    ('tgen_20250510_120000_016', 'ORDER_20250510120016', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 16 (Dave - 다른 예약)

    -- 감성 테마 결제들 (32,000원) - 5월 11일
    ('tgen_20250510_133000_017', 'ORDER_20250510133017', 1000, 'NORMAL', 'APPROVED'), -- Payment ID: 17 (Grace - 다른 예약)
    ('tgen_20250510_140000_018', 'ORDER_20250510140018', 1000, 'NORMAL', 'APPROVED');
-- Payment ID: 18 (Heidi - 다른 예약)


-- 외래키가 포함된 예약 데이터 재생성
INSERT INTO reservations(reservation_slot_id, member_id, created_at, reservation_status, payment_id)
VALUES
    -- ReservationSlot ID 1 (논리, 5/10, 08:00) - Alice(CONFIRMED), Bob(REQUESTED), Carol(REQUESTED)
    (1, 3, '2025-05-09 10:00:00', 'CONFIRMED', 1),      -- Alice - Payment ID: 1
    (1, 4, '2025-05-09 10:30:00', 'REQUESTED', NULL),   -- Bob - 대기중, 결제 없음
    (1, 5, '2025-05-09 11:00:00', 'REQUESTED', NULL),   -- Carol - 대기중, 결제 없음

    -- ReservationSlot ID 2 (논리, 5/10, 12:00) - Dave(CONFIRMED), Eve(REQUESTED)
    (2, 6, '2025-05-09 11:30:00', 'CONFIRMED', 2),      -- Dave - Payment ID: 2
    (2, 7, '2025-05-09 12:00:00', 'REQUESTED', NULL),   -- Eve - 대기중, 결제 없음

    -- ReservationSlot ID 3 (논리, 5/10, 14:00) - Frank(FAILED)
    (3, 8, '2025-05-09 12:30:00', 'FAILED', NULL),      -- Frank - 실패, 결제 없음

    -- ReservationSlot ID 4 (논리, 5/10, 16:00) - Grace(CONFIRMED), Heidi(REQUESTED)
    (4, 9, '2025-05-09 13:00:00', 'CONFIRMED', 3),      -- Grace - Payment ID: 3
    (4, 10, '2025-05-09 13:30:00', 'REQUESTED', NULL),  -- Heidi - 대기중, 결제 없음

    -- ReservationSlot ID 5 (논리, 5/10, 18:00) - Ivan(CONFIRMED), Judy(REQUESTED), Mallory(REQUESTED)
    (5, 11, '2025-05-09 14:00:00', 'CONFIRMED', 4),     -- Ivan - Payment ID: 4
    (5, 12, '2025-05-09 14:30:00', 'REQUESTED', NULL),  -- Judy - 대기중, 결제 없음
    (5, 13, '2025-05-09 15:00:00', 'REQUESTED', NULL),  -- Mallory - 대기중, 결제 없음

    -- ReservationSlot ID 6 (로맨스, 5/10, 08:00) - Niaj(CONFIRMED), Olivia(REQUESTED)
    (6, 14, '2025-05-09 15:30:00', 'CONFIRMED', 5),     -- Niaj - Payment ID: 5
    (6, 15, '2025-05-09 16:00:00', 'REQUESTED', NULL),  -- Olivia - 대기중, 결제 없음

    -- ReservationSlot ID 7 (로맨스, 5/10, 12:00) - Peggy(CONFIRMED)
    (7, 16, '2025-05-09 16:30:00', 'CONFIRMED', 6),     -- Peggy - Payment ID: 6

    -- ReservationSlot ID 8 (로맨스, 5/10, 14:00) - Rupert(CONFIRMED), Sybil(REQUESTED)
    (8, 17, '2025-05-09 17:00:00', 'CONFIRMED', 7),     -- Rupert - Payment ID: 7
    (8, 18, '2025-05-09 17:30:00', 'REQUESTED', NULL),  -- Sybil - 대기중, 결제 없음

    -- ReservationSlot ID 9 (로맨스, 5/10, 16:00) - Trent(CONFIRMED), Uma(REQUESTED)
    (9, 19, '2025-05-09 18:00:00', 'CONFIRMED', 8),     -- Trent - Payment ID: 8
    (9, 20, '2025-05-09 18:30:00', 'REQUESTED', NULL),  -- Uma - 대기중, 결제 없음

    -- ReservationSlot ID 10 (로맨스, 5/10, 18:00) - Victor(CONFIRMED)
    (10, 21, '2025-05-09 19:00:00', 'CONFIRMED', 9),    -- Victor - Payment ID: 9

    -- ReservationSlot ID 11 (모험, 5/10, 08:00) - Wendy(CONFIRMED), Xander(REQUESTED)
    (11, 22, '2025-05-09 19:30:00', 'CONFIRMED', 10),   -- Wendy - Payment ID: 10
    (11, 23, '2025-05-09 20:00:00', 'REQUESTED', NULL), -- Xander - 대기중, 결제 없음

    -- ReservationSlot ID 12 (모험, 5/10, 12:00) - Yvonne(CONFIRMED), Zack(REQUESTED)
    (12, 24, '2025-05-09 20:30:00', 'CONFIRMED', 11),   -- Yvonne - Payment ID: 11
    (12, 25, '2025-05-09 21:00:00', 'REQUESTED', NULL), -- Zack - 대기중, 결제 없음

    -- ReservationSlot ID 13 (모험, 5/10, 14:00) - Amy(CONFIRMED)
    (13, 26, '2025-05-09 21:30:00', 'CONFIRMED', 12),   -- Amy - Payment ID: 12

    -- ReservationSlot ID 14 (모험, 5/10, 16:00) - Brian(CONFIRMED), Chloe(REQUESTED)
    (14, 27, '2025-05-09 22:00:00', 'CONFIRMED', 13),   -- Brian - Payment ID: 13
    (14, 28, '2025-05-09 22:30:00', 'REQUESTED', NULL), -- Chloe - 대기중, 결제 없음

    -- 5월 11일 예약들
    -- ReservationSlot ID 15 (SF, 5/11, 08:00) - Daniel(CONFIRMED), Alice(REQUESTED)
    (15, 29, '2025-05-10 10:00:00', 'CONFIRMED', 14),   -- Daniel - Payment ID: 14
    (15, 3, '2025-05-10 10:30:00', 'REQUESTED', NULL),  -- Alice - 대기중, 결제 없음 (다른 예약)

    -- ReservationSlot ID 16 (SF, 5/11, 12:00) - Bob(CONFIRMED), Carol(REQUESTED)
    (16, 4, '2025-05-10 11:00:00', 'CONFIRMED', 15),    -- Bob - Payment ID: 15 (다른 예약)
    (16, 5, '2025-05-10 11:30:00', 'REQUESTED', NULL),  -- Carol - 대기중, 결제 없음 (다른 예약)

    -- ReservationSlot ID 17 (SF, 5/11, 14:00) - Dave(CONFIRMED), Eve(REQUESTED), Frank(REQUESTED)
    (17, 6, '2025-05-10 12:00:00', 'CONFIRMED', 16),    -- Dave - Payment ID: 16 (다른 예약)
    (17, 7, '2025-05-10 12:30:00', 'REQUESTED', NULL),  -- Eve - 대기중, 결제 없음 (다른 예약)
    (17, 8, '2025-05-10 13:00:00', 'REQUESTED', NULL),  -- Frank - 대기중, 결제 없음 (다른 예약)

    -- ReservationSlot ID 18 (감성, 5/11, 08:00) - Grace(CONFIRMED)
    (18, 9, '2025-05-10 13:30:00', 'CONFIRMED', 17),    -- Grace - Payment ID: 17 (다른 예약)

    -- ReservationSlot ID 19 (감성, 5/11, 12:00) - Heidi(CONFIRMED), Ivan(REQUESTED)
    (19, 10, '2025-05-10 14:00:00', 'CONFIRMED', 18),   -- Heidi - Payment ID: 18 (다른 예약)
    (19, 11, '2025-05-10 14:30:00', 'REQUESTED', NULL); -- Ivan - 대기중, 결제 없음 (다른 예약)

