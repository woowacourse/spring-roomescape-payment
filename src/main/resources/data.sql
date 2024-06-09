INSERT INTO RESERVATION_TIME(START_AT)
VALUES ('15:00'),
       ('16:00'),
       ('17:00'),
       ('18:00'),
       ('19:00');

INSERT INTO THEME(NAME, DESCRIPTION, THUMBNAIL)
VALUES ('봄', '설명1', 'https://raw.githubusercontent.com/reddevilmidzy/spring-roomescape-payment/step2/png/%EA%B3%A0%EB%82%9C%EC%97%AD%EA%B2%BD%EA%B7%B9%EB%B3%B5%EA%B8%B0.jpg'),
       ('여름', '설명2', 'https://raw.githubusercontent.com/reddevilmidzy/spring-roomescape-payment/step2/png/%EB%82%B4%EA%B0%80%EA%B1%8D%ED%95%98%EC%A7%80.jpg'),
       ('가을', '설명3', 'https://raw.githubusercontent.com/reddevilmidzy/spring-roomescape-payment/step2/png/%EC%9A%B8%EC%96%B4%EB%8F%84%EB%8F%BC.jpg'),
       ('겨울', '설명4', 'https://raw.githubusercontent.com/reddevilmidzy/spring-roomescape-payment/step2/png/%EC%9D%B4%EC%A0%9C%EB%8A%94%EB%8D%94%EC%9D%B4%EC%83%81.jpg');

INSERT INTO MEMBER(NAME, EMAIL, PASSWORD, ROLE)
VALUES ('레디', 'redddy@gmail.com', '3e2b2d79b2f6f90ba2f3ae18a90ae990b149b2fdbc208f55ac9b763b4dad0a16', 'ADMIN'),
       ('재즈', 'gkatjraud1@redddybabo.com', 'f8e5bc8fdaca1bccab597398b0f26a814c1486fb8a37515c075f4ff023abe726', 'USER'),
       ('제제', 'jinwuo0925@gmail.com', '4e5ef62cc65465fbd14118b6e894aa0fa3fbe72c31fcdb6829a3ea21163b3e3a', 'USER');

INSERT INTO RESERVATION(MEMBER_ID, DATE, TIME_ID, THEME_ID)
VALUES (1, CURRENT_DATE - 3, 1, 1),
       (2, CURRENT_DATE - 2, 3, 2),
       (1, CURRENT_DATE - 1, 2, 2),
       (2, CURRENT_DATE - 1, 1, 2),
       (3, CURRENT_DATE - 7, 1, 3),
       (3, CURRENT_DATE + 3, 4, 3),
       (2, CURRENT_DATE + 4, 4, 3),
       (3, CURRENT_DATE + 4, 4, 3);

INSERT INTO PAYMENT(ORDER_ID, PAYMENT_KEY, ORDER_NAME, TOTAL_AMOUNT, RESERVATION_ID)
VALUES ('order_id1', 'payment_key1', '방탈출1', 1000, 1),
       ('order_id2', 'payment_key2', '방탈출2', 1000, 2),
       ('order_id3', 'payment_key3', '방탈출2', 1000, 3),
       ('order_id4', 'payment_key4', '방탈출2', 1000, 4),
       ('order_id5', 'payment_key5', '방탈출3', 1000, 5),
       ('order_id6', 'payment_key6', '방탈출3', 1000, 6),
       ('order_id7', 'payment_key7', '방탈출3', 1000, 7),
       ('order_id8', 'payment_key8', '방탈출3', 1000, 8);
