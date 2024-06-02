SET REFERENTIAL_INTEGRITY FALSE;
truncate table reservation_time;
truncate table reservation;
truncate table member;
truncate table theme;
truncate table waiting;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('10:00', current_timestamp, current_timestamp);
INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('12:00', current_timestamp, current_timestamp);
INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('14:00', current_timestamp, current_timestamp);
INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('16:00', current_timestamp, current_timestamp);
INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('18:00', current_timestamp, current_timestamp);
INSERT INTO reservation_time (start_at, created_at, modified_at) VALUES ('20:00', current_timestamp, current_timestamp);

INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('공포', '무서워', 'https://zerolotteworld.com/storage/WAYH10LvyaCuAb9ndj1apZIpzEAdpjeAhPR7Gb7J.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('액션', '신나', 'https://sherlock-holmes.co.kr/attach/theme/17000394031.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('SF', '신기해', 'https://sherlock-holmes.co.kr/attach/theme/16941579841.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('로맨스', '달달해', 'https://i.postimg.cc/vDFKqct1/theme.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('코미디', '웃기다', 'https://sherlock-holmes.co.kr/attach/theme/16956118601.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('드라마', '반전', 'https://sherlock-holmes.co.kr/attach/theme/16941579841.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('잠입', '스릴있어', 'https://search.pstatic.net/sunny/?src=https%3A%2F%2Ffile.miricanvas.com%2Ftemplate_thumb%2F2022%2F05%2F15%2F13%2F50%2Fk2nje40j0jwztqza%2Fthumb.jpg&type=sc960_832', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('오락', '재밌어', 'http://jamsil.cubeescape.co.kr/theme/basic_room2/img/rain/room15.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('판타지', '말이 안돼', 'https://i.postimg.cc/8k2PQ4yv/theme.jpg', current_timestamp, current_timestamp);
INSERT INTO theme (name, description, thumbnail, created_at, modified_at) VALUES ('감성', '감동적', 'https://sherlock-holmes.co.kr/attach/theme/16788523411.jpg', current_timestamp, current_timestamp);

INSERT INTO member (name, role, email, password, created_at, modified_at) values ( '몰리', 'USER', 'hihi@naver.com', 'hihi', current_timestamp, current_timestamp);
INSERT INTO member (name, role, email, password, created_at, modified_at) values ( '비밥', 'ADMIN', 'bibap@naver.com', 'hihi', current_timestamp, current_timestamp);
INSERT INTO member (name, role, email, password, created_at, modified_at) values ( '포비', 'ADMIN', 'test@naver.com', 'hihi', current_timestamp, current_timestamp);

INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 1, '2024-04-23', 1, 1, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 2, '2024-04-24', 2, 1, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 3, '2024-04-25', 3, 1, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 1, '2024-04-26', 4, 1, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 2, '2024-04-27', 5, 2, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 3, '2024-05-01', 4, 3, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 3, '2024-05-28', 1, 2, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 1, '2024-05-29', 2, 2, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 2, '2024-05-30', 3, 3, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 1, '2024-11-02', 5, 4, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 2, '2024-11-03', 1, 4, current_timestamp, current_timestamp);
INSERT INTO reservation (member_id, date, reservation_time_id, theme_id, created_at, modified_at) values ( 3, '2024-11-04', 2, 5, current_timestamp, current_timestamp);

INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (1, 1, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (1, 2, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (2, 2, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (3, 2, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (5, 2, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (1, 3, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (2, 3, current_timestamp, current_timestamp);
INSERT INTO waiting (reservation_id, member_id, created_at, modified_at) VALUES (3, 3, current_timestamp, current_timestamp);
