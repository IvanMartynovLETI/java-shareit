INSERT INTO users(user_name, email) values ('user1', 'user1@yandex.ru')
INSERT INTO users(user_name, email) values ('user2', 'user2@yandex.ru')
INSERT INTO users(user_name, email) values ('user3', 'user3@yandex.ru')
INSERT INTO items(item_name, description, available, owner_id) values ('item1', 'description of item1', true, 1)
INSERT INTO bookings(start_time, end_time, item_id, booker_id, status) values ('2022-08-05 18:36', '2022-08-05 19:00', 1, 2, 'APPROVED')
INSERT INTO item_requests(description, requestor_id, created) values ('description of request1', 1,'2022-08-05 18:36')