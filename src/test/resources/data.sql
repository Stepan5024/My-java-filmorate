MERGE INTO "User" AS target
USING (
    SELECT 1 AS "ID", 'test@example.com' AS "Email", 'testlogin' AS "Login", 'Test User' AS "Name", '1990-01-01' AS "Birthday"
) AS source
ON (target."ID" = source."ID")
WHEN MATCHED THEN
    UPDATE SET
        target."Email" = source."Email",
        target."Login" = source."Login",
        target."Name" = source."Name",
        target."Birthday" = source."Birthday"
WHEN NOT MATCHED THEN
    INSERT ("ID", "Email", "Login", "Name", "Birthday")
    VALUES (source."ID", source."Email", source."Login", source."Name", source."Birthday");



MERGE INTO "MPARating" AS target USING (
    VALUES ('G', 'Фильм подходит для всех возрастов'),
        ('PG', 'Рекомендуется присутствие родителей'),
        (
            'PG-13',
            'Рекомендуется осторожность при просмотре детям до 13 лет'
        ),
        (
            'R',
            'Доступ к фильму лицам до 17 лет только с согласия взрослых'
        ),
        (
            'NC-17',
            'Доступ к фильму лицам до 18 лет запрещён'
        )
) AS source ("Name", "Description") ON target."Name" = source."Name"
WHEN NOT MATCHED THEN
INSERT ("Name", "Description")
VALUES (source."Name", source."Description");

-- Создаем временную таблицу для хранения временных данных
CREATE TEMPORARY TABLE IF NOT EXISTS "temp_genre" (
    "ID" SERIAL PRIMARY KEY,
    "Name" VARCHAR(255) NOT NULL
);
-- Вставляем данные во временную таблицу
INSERT INTO "temp_genre" ("Name")
VALUES ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');
-- Вставляем данные из временной таблицы в основную таблицу Genre, если их там нет
INSERT INTO "Genre" ("Name")
SELECT tg."Name"
FROM "temp_genre" tg
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre" g
        WHERE g."Name" = tg."Name"
    );
-- Удаляем временную таблицу
DROP TABLE IF EXISTS "temp_genre";

INSERT INTO "FriendStatus" ("ID", "Name")
SELECT 1,
    'Неподтверждённая'
WHERE NOT EXISTS (
        SELECT 1
        FROM "FriendStatus"
        WHERE "ID" = 1
    );
INSERT INTO "FriendStatus" ("ID", "Name")
SELECT 2,
    'Подтверждённая'
WHERE NOT EXISTS (
        SELECT 1
        FROM "FriendStatus"
        WHERE "ID" = 2
    );