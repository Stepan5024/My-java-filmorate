CREATE TABLE IF NOT EXISTS "Film" (
    "ID" IDENTITY PRIMARY KEY,
    "Name" nvarchar(255) NOT NULL,
    "Description" nvarchar(max) NOT NULL,
    "ReleaseDate" date NOT NULL,
    "Duration" int NOT NULL,
    "MPARatingID" int NOT NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY ("ID")
);
CREATE TABLE IF NOT EXISTS "MPARating" (
    "ID" IDENTITY PRIMARY KEY,
    "Name" nvarchar(10) NOT NULL UNIQUE,
    "Description" nvarchar(100) NOT NULL,
    CONSTRAINT "pk_MPARating" PRIMARY KEY ("ID"),
    CONSTRAINT "uc_MPARating_Rating" UNIQUE ("Name")
);
CREATE TABLE IF NOT EXISTS "Genre" (
    "ID" IDENTITY PRIMARY KEY,
    "Name" nvarchar(100) NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY ("ID"),
    CONSTRAINT "uc_Genre_Name" UNIQUE ("Name")
);
CREATE TABLE IF NOT EXISTS "GenreInFilm" (
    "ID" IDENTITY PRIMARY KEY,
    "FilmID" int NOT NULL,
    "GenreID" int NOT NULL,
    CONSTRAINT "pk_GenreInFilm" PRIMARY KEY ("ID")
);
CREATE TABLE IF NOT EXISTS "User" (
    "ID" IDENTITY PRIMARY KEY,
    "Email" varchar(255) NOT NULL,
    "Login" varchar(255) NOT NULL,
    "Name" varchar(255) NOT NULL,
    "Birthday" date NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY ("ID"),
    CONSTRAINT "uc_User_Email" UNIQUE ("Email"),
    CONSTRAINT "uc_User_Login" UNIQUE ("Login")
);
CREATE TABLE IF NOT EXISTS "UserFilmLike" (
    "ID" IDENTITY PRIMARY KEY,
    "UserID" int NOT NULL,
    "FilmID" int NOT NULL,
    CONSTRAINT "pk_UserFilmLike" PRIMARY KEY ("ID")
);
CREATE TABLE IF NOT EXISTS "UserFriend" (
    "ID" IDENTITY PRIMARY KEY,
    "UserID" int NOT NULL,
    "FriendID" int NOT NULL,
    "Status" varchar(20) NOT NULL,
    CONSTRAINT "pk_UserFriend" PRIMARY KEY ("ID"),
    CONSTRAINT "fk_UserFriend_UserID" FOREIGN KEY ("UserID") REFERENCES "User" ("ID"),
    CONSTRAINT "fk_UserFriend_FriendID" FOREIGN KEY ("FriendID") REFERENCES "User" ("ID"),
    CONSTRAINT "uc_UserFriend_User_Friend" UNIQUE ("UserID", "FriendID")
);
CREATE TABLE IF NOT EXISTS "FriendStatus" (
    "ID" IDENTITY PRIMARY KEY,
    "Name" nvarchar(100) NOT NULL,
    CONSTRAINT "pk_FriendStatus" PRIMARY KEY ("ID"),
    CONSTRAINT "uc_FriendStatus_Name" UNIQUE ("Name")
);
-- Проверка существования и добавление внешних ключей
ALTER TABLE "Film"
ADD CONSTRAINT IF NOT EXISTS "fk_Film_MPARatingID" FOREIGN KEY("MPARatingID") REFERENCES "MPARating" ("ID");
ALTER TABLE "GenreInFilm"
ADD CONSTRAINT IF NOT EXISTS "fk_GenreInFilm_FilmID" FOREIGN KEY("FilmID") REFERENCES "Film" ("ID");
ALTER TABLE "GenreInFilm"
ADD CONSTRAINT IF NOT EXISTS "fk_GenreInFilm_GenreID" FOREIGN KEY("GenreID") REFERENCES "Genre" ("ID");
ALTER TABLE "UserFilmLike"
ADD CONSTRAINT IF NOT EXISTS "fk_UserFilmLike_UserID" FOREIGN KEY("UserID") REFERENCES "User" ("ID");
ALTER TABLE "UserFilmLike"
ADD CONSTRAINT IF NOT EXISTS "fk_UserFilmLike_FilmID" FOREIGN KEY("FilmID") REFERENCES "Film" ("ID");
ALTER TABLE "UserFriend"
ADD CONSTRAINT IF NOT EXISTS "fk_UserFriend_Status" FOREIGN KEY ("Status") REFERENCES "FriendStatus" ("ID");