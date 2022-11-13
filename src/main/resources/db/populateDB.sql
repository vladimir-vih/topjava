DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);


INSERT INTO meals (date_time, description, calories, user_id)
VALUES ((TIMESTAMP '2020-01-30 10:00:00'), 'Завтрак', 1000, 100000),
       ((TIMESTAMP '2020-01-30 14:00:00'), 'Обед', 1500, 100000),
       ((TIMESTAMP '2020-01-30 23:59:59'), 'Ужин', 501, 100000),
       ((TIMESTAMP '2020-01-31 11:30:00'), 'Завтрак', 1000, 100000),
       ((TIMESTAMP '2020-01-31 19:40:00'), 'Ужин', 450, 100000),
       ((TIMESTAMP '2020-01-31 10:00:00'), 'Завтрак', 500, 100001),
       ((TIMESTAMP '2020-01-31 14:00:00'), 'Обед', 1500, 100001),
       ((TIMESTAMP '2020-01-31 19:00:00'), 'Ужин', 450, 100001);