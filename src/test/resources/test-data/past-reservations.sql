insert into reservation (id, detail_id, member_id, status, created_at)
values (101, 21, 1, 'RESERVED', dateadd('day', -1, current_date)),
       (102, 22, 2, 'WAITING', dateadd('day', -1, current_date)),
       (103, 23, 3, 'WAITING', dateadd('day', -1, current_date)),
       (104, 24, 4, 'WAITING', dateadd('day', -1, current_date)),
       (108, 25, 1, 'RESERVED', dateadd('day', -2, current_date)),
       (109, 26, 1, 'WAITING', dateadd('day', -2, current_date)),
       (1010, 27, 1, 'WAITING', dateadd('day', -2, current_date)),
       (1011, 28, 1, 'RESERVED', dateadd('day', -3, current_date)),
       (1012, 29, 1, 'RESERVED', dateadd('day', -3, current_date)),
       (1013, 30, 1, 'RESERVED', dateadd('day', -3, current_date)),
       (1014, 20, 1, 'RESERVED', dateadd('day', -4, current_date)),
       (1015, 20, 1, 'RESERVED', dateadd('day', -4, current_date)),
       (1016, 20, 1, 'RESERVED', dateadd('day', -4, current_date)),
       (1017, 20, 1, 'WAITING', dateadd('day', -4, current_date));