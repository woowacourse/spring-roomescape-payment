INSERT INTO reservation_time(start_at)
VALUES ('10:00');
INSERT INTO reservation_time(start_at)
VALUES ('12:00');
INSERT INTO reservation_time(start_at)
VALUES ('14:00');
INSERT INTO reservation_time(start_at)
VALUES ('16:00');
INSERT INTO reservation_time(start_at)
VALUES ('18:00');

INSERT INTO theme(name, description, thumbnail, price)
VALUES ('공포', '덜덜 무서워요',
        'https://png.pngtree.com/png-vector/20240919/ourlarge/pngtree-scary-ghost-illustration-for-horror-events-png-image_13704874.png',
        10000);
INSERT INTO theme(name, description, thumbnail, price)
VALUES ('추리', '흠흠 추리해보세요',
        'https://cdn-icons-png.flaticon.com/512/12469/12469179.png',
        8000);
INSERT INTO theme(name, description, thumbnail, price)
VALUES ('코믹', '깔깔 재밌어요',
        'https://img.freepik.com/free-vector/comic-style-blank-frame-background-with-halftone-effect-vector_1017-48508.jpg?semt=ais_hybrid&w=740',
        5000);
INSERT INTO theme(name, description, thumbnail, price)
VALUES ('판타지', '와우와우 판타지',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/Variance_in_character_design_-_Lia_Turtle%2C_Shain%2C_and_Cendrea_from_Chaos%26Evolutions.png/500px-Variance_in_character_design_-_Lia_Turtle%2C_Shain%2C_and_Cendrea_from_Chaos%26Evolutions.png',
        15000);


INSERT INTO member(name, email, password, role)
VALUES ('우테코', 'wooteco@gmail.com', '$2a$10$HPuLMfygOsN.3UIEqvcBwOS/uaOS4cJ0EQb/eeqexol7BaiGMSXXi', 'admin');
INSERT INTO member(name, email, password, role)
VALUES ('에드', 'ed@gmail.com', '$2a$10$HPuLMfygOsN.3UIEqvcBwOS/uaOS4cJ0EQb/eeqexol7BaiGMSXXi', 'user');
INSERT INTO member(name, email, password, role)
VALUES ('파랑', 'parang@gmail.com', '$2a$10$HPuLMfygOsN.3UIEqvcBwOS/uaOS4cJ0EQb/eeqexol7BaiGMSXXi', 'user');

INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-05', 5, 1, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-04', 1, 4, 3, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-02', 4, 2, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-04', 5, 3, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-06', 3, 1, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-06', 3, 2, 3, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-07', 4, 4, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-07', 1, 1, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-04', 5, 3, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-05', 2, 4, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-06', 4, 1, 3, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-02', 3, 3, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-02', 2, 2, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-03', 1, 4, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-03', 2, 1, 3, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-04', 5, 3, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-04', 4, 2, 3, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-07', 1, 4, 2, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-07', 2, 3, 1, 'APPROVED');
INSERT INTO reservation(date, time_id, theme_id, member_id, reservation_state)
VALUES ('2025-06-09', 1, 2, 3, 'APPROVED');

-- ON-SITE (id: 1, 6, 8, 11, 13, 15, 20)
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (1, 'ONSITE', 'APPROVED', 1);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (6, 'ONSITE', 'APPROVED', 6);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (8, 'ONSITE', 'APPROVED', 8);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (11, 'ONSITE', 'APPROVED', 11);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (13, 'ONSITE', 'APPROVED', 13);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (15, 'ONSITE', 'APPROVED', 15);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (20, 'ONSITE', 'APPROVED', 20);

-- PAYMENT (id: 2, 3, 5, 7, 17, 18, 19)
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (2, 'PAYMENT', 'APPROVED', 2);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (3, 'PAYMENT', 'APPROVED', 3);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (5, 'PAYMENT', 'APPROVED', 5);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (7, 'PAYMENT', 'APPROVED', 7);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (17, 'PAYMENT', 'APPROVED', 17);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (18, 'PAYMENT', 'APPROVED', 18);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (19, 'PAYMENT', 'APPROVED', 19);

-- ADMIN_APPROVAL (id: 4, 9, 10, 12, 14, 16)
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (4, 'ADMIN_APPROVAL', 'APPROVED', 4);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (9, 'ADMIN_APPROVAL', 'APPROVED', 9);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (10, 'ADMIN_APPROVAL', 'APPROVED', 10);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (12, 'ADMIN_APPROVAL', 'APPROVED', 12);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (14, 'ADMIN_APPROVAL', 'APPROVED', 14);
INSERT INTO approval(id, approval_type, approval_status, reservation_id)
VALUES (16, 'ADMIN_APPROVAL', 'APPROVED', 16);

INSERT INTO onsite(id, amount)
VALUES (1, 10000);
INSERT INTO onsite(id, amount)
VALUES (6, 10000);
INSERT INTO onsite(id, amount)
VALUES (8, 10000);
INSERT INTO onsite(id, amount)
VALUES (11, 10000);
INSERT INTO onsite(id, amount)
VALUES (13, 10000);
INSERT INTO onsite(id, amount)
VALUES (15, 10000);
INSERT INTO onsite(id, amount)
VALUES (20, 10000);

INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (2, 'ORDER002', 'KEY002', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (3, 'ORDER003', 'KEY003', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (5, 'ORDER005', 'KEY005', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (7, 'ORDER007', 'KEY007', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (17, 'ORDER017', 'KEY017', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (18, 'ORDER018', 'KEY018', 10000);
INSERT INTO payment(id, order_id, payment_key, amount)
VALUES (19, 'ORDER019', 'KEY019', 10000);

INSERT INTO admin_approval(id, member_id)
VALUES (4, 1);
INSERT INTO admin_approval(id, member_id)
VALUES (9, 1);
INSERT INTO admin_approval(id, member_id)
VALUES (10, 1);
INSERT INTO admin_approval(id, member_id)
VALUES (12, 1);
INSERT INTO admin_approval(id, member_id)
VALUES (14, 1);
INSERT INTO admin_approval(id, member_id)
VALUES (16, 1);

ALTER TABLE approval
    ALTER COLUMN id RESTART WITH 21;
