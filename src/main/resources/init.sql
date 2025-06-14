INSERT INTO theme (name, description, thumbnail)
VALUES ('마피아 할머니의 비밀창고', '47년전 활동했던 할머니의 비밀창고 그 안에는 무엇이 있을까...?',
        'https://sdmntprwestus.oaiusercontent.com/files/00000000-fd58-6230-9819-07ef8a6b4b6c/raw?se=2025-06-07T10%3A44%3A22Z&sp=r&sv=2024-08-04&sr=b&scid=3c60d5fc-2841-52e2-9f7f-2d127c6a2243&skoid=61180a4f-34a9-42b7-b76d-9ca47d89946d&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-06-07T01%3A13%3A29Z&ske=2025-06-08T01%3A13%3A29Z&sks=b&skv=2024-08-04&sig=q8KsF8c0YQBueYVj8fWIBTjFbmsS8bVj%2BRk08qCvkJY%3D');
INSERT INTO theme (name, description, thumbnail)
VALUES ('저주들린 오두막 집', '버려진 오두막 집 밤만 되면 불이 켜진다는 소문이...',
        'https://sdmntprwestus.oaiusercontent.com/files/00000000-9904-6230-b4bd-9a7e0c69fe55/raw?se=2025-06-07T10%3A46%3A13Z&sp=r&sv=2024-08-04&sr=b&scid=b31d3107-6602-5a6b-afe9-5d98d7f2decb&skoid=61180a4f-34a9-42b7-b76d-9ca47d89946d&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-06-07T01%3A14%3A50Z&ske=2025-06-08T01%3A14%3A50Z&sks=b&skv=2024-08-04&sig=gvYPknZ49XUBIvY9AZg77HlSYdQ6CkmWhfPnsQS8Eyw%3D');
INSERT INTO theme (name, description, thumbnail)
VALUES ('우리 엄마는 7살', '나는 25살..',
        'https://sdmntprwestus.oaiusercontent.com/files/00000000-a504-6230-9850-6bceda04fb72/raw?se=2025-06-07T10%3A47%3A32Z&sp=r&sv=2024-08-04&sr=b&scid=d189685d-f94f-5e87-ae63-c6cfd5d0b8f0&skoid=61180a4f-34a9-42b7-b76d-9ca47d89946d&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-06-07T01%3A13%3A30Z&ske=2025-06-08T01%3A13%3A30Z&sks=b&skv=2024-08-04&sig=mKO9KkJjBV2ek2pQ%2B2Z1j1FEMCxzh%2BCblhhTD5tGm9Q%3D');
INSERT INTO theme (name, description, thumbnail)
VALUES ('외계인의 침공', '삐리 빠라 뽕, 쁘르르 쁘르르 빠따 삠',
        'https://sdmntprwestus.oaiusercontent.com/files/00000000-cd90-6230-b3dd-d826aefcffbb/raw?se=2025-06-07T10%3A48%3A51Z&sp=r&sv=2024-08-04&sr=b&scid=d46ead27-7260-593f-95d5-a2415c98f9da&skoid=61180a4f-34a9-42b7-b76d-9ca47d89946d&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-06-07T01%3A14%3A52Z&ske=2025-06-08T01%3A14%3A52Z&sks=b&skv=2024-08-04&sig=iDbJfgNNGiNLxjCqS/XaGC0Fd%2Bb0SME1Ymz9LDlAiE4%3D');

INSERT INTO reservation_time (start_at)
VALUES ('12:00');
INSERT INTO reservation_time (start_at)
VALUES ('14:00');
INSERT INTO reservation_time (start_at)
VALUES ('16:00');
INSERT INTO reservation_time (start_at)
VALUES ('18:00');
INSERT INTO reservation_time (start_at)
VALUES ('20:00');

INSERT INTO member (name, email, password, role)
VALUES ('포라', 'forarium20@gmail.com', '1234', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('코기', 'ind07152@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('율무', 'ind07162@naver.com', 'asd', 'USER');
INSERT INTO member (name, email, password, role)
VALUES ('ADMIN', 'admin@naver.com', '1234', 'ADMIN');
INSERT INTO member (name, email, password, role)
VALUES ('가이온', 'jumdo12@gmail.com', '1234', 'USER');

INSERT INTO reservation (date, member_id, time_id, theme_id, payment_id, reservation_status)
VALUES ('2025-06-07', 4, 3, 1, null, 'PENDING');
INSERT INTO reservation (date, member_id, time_id, theme_id, payment_id, reservation_status)
VALUES ('2025-06-06', 4, 3, 1, null, 'PENDING');
INSERT INTO reservation (date, member_id, time_id, theme_id, payment_id, reservation_status)
VALUES ('2025-06-05', 4, 3, 2, null, 'PENDING');
INSERT INTO reservation (date, member_id, time_id, theme_id, payment_id, reservation_status)
VALUES ('2025-06-05', 4, 3, 4, null, 'PENDING');
