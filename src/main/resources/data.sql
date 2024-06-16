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
) AS source ("Rating", "Description") ON target."Rating" = source."Rating"
WHEN NOT MATCHED THEN
INSERT ("Rating", "Description")
VALUES (source."Rating", source."Description");

INSERT INTO "Genre" ("Name")
SELECT 'Комедия'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Комедия'
    );
INSERT INTO "Genre" ("Name")
SELECT 'Драма'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Драма'
    );
INSERT INTO "Genre" ("Name")
SELECT 'Мультфильм'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Мультфильм'
    );
INSERT INTO "Genre" ("Name")
SELECT 'Триллер'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Триллер'
    );
INSERT INTO "Genre" ("Name")
SELECT 'Документальный'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Документальный'
    );
INSERT INTO "Genre" ("Name")
SELECT 'Боевик'
WHERE NOT EXISTS (
        SELECT 1
        FROM "Genre"
        WHERE "Name" = 'Боевик'
    );

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