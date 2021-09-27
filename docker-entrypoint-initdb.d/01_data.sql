INSERT INTO users(id, username, password)
VALUES (1, 'admin', '$argon2id$v=19$m=4096,t=3,p=1$CjM/3pdG9sYDzxGFDbesMA$HA1Fm8y23U9QucCFmq1hynPzyCsMfSNKpEqyZVif/og'),
       (2, 'moderator', '$argon2id$v=19$m=4096,t=3,p=1$CjM/3pdG9sYDzxGFDbesMA$HA1Fm8y23U9QucCFmq1hynPzyCsMfSNKpEqyZVif/og'),
       (3, 'student', '$argon2id$v=19$m=4096,t=3,p=1$gCAC+zI1HRM5IRKJc8HhGw$RadiUVwSqt6m7icr6+Cp67Jh8Sc+YRs+zJ9xw2nYh6M'),
       (4, 'user', '$argon2id$v=19$m=4096,t=3,p=1$esF45tINfs5fBphaUtAGyA$3scigIWMbdCa3t5sI734dlf7ic9NyX9+s+Hg/s+bhC8');

ALTER SEQUENCE users_id_seq RESTART WITH 5;

INSERT INTO tokens(token, "userId")
VALUES ('6NSb+2kcdKF44ut4iBu+dm6YLu6pakWapvxHtxqaPgMr5iRhox/HlhBerAZMILPjwnRtXms+zDfVTLCsao9nuw==', 1),
       ('PgKam1JWhAvmaZT06RkRgay0E08jOOYC2YbDuIsbg+3ATgEvrDit6idDYIKi6vc8m/XdSWseDX9vMvwWewhaXw==', 2),
       ('hp2dqW7TyVkXP34ieOjtBYbBJ7K0TOn8FsyC5RfUysUoEmhomH4UoCilK09Ttt/oNu0bGU2vTW9G4Si9iT261w==', 3),
       ('51yCM3QXCUvLBLQy7Eo6GUYLq17iy/kxBmI5HikOcvtqJzMefB+kJWBpM2UaoKHvuj0eCxAEKqoMDyJ90Le2mA==', 4);


INSERT INTO cards(id, "ownerId", number, balance)
VALUES (1, 1, '**** *111', 150000),
       (2, 1, '**** *112', 190000),
       (3, 2, '**** *222', 50000),
       (4, 2, '**** *223', 90000),
       (5, 3, '**** *333', 37000),
       (6, 3, '**** *334', 42000),
       (7, 4, '**** *444', 137000),
       (8, 4, '**** *445', 142000);

ALTER SEQUENCE cards_id_seq RESTART WITH 9;

INSERT INTO news(title, text, created)
VALUES ('news 1', 'aaaaaaaaaaaaaaa', '2021-06-03 15:46:13.480253 +03:00'),
       ('news 2', 'bbbbbbbbbbbbbbb', '2021-07-15 05:36:17.720154 +03:00'),
       ('news 3', 'ccccccccccccccc', '2021-08-27 14:56:08.330143 +03:00'),
       ('news 4', 'ddddddddddddddd', '2021-09-03 08:16:24.150143 +03:00');


