-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/Qndfao
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "Film" (
    "ID" int   NOT NULL,
    "Name" nvarchar(255)   NOT NULL,
    "Description" nvarchar(max)   NOT NULL,
    "ReleaseDate" date   NOT NULL,
    "Duration" int   NOT NULL,
    "MPARatingID" int   NOT NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY (
        "ID"
     )
);

CREATE TABLE "MPARating" (
    "ID" int   NOT NULL,
    "Rating" nvarchar(10)   NOT NULL,
    "Description" nvarchar(100)   NOT NULL,
    CONSTRAINT "pk_MPARating" PRIMARY KEY (
        "ID"
     ),
    CONSTRAINT "uc_MPARating_Rating" UNIQUE (
        "Rating"
    )
);

CREATE TABLE "Genre" (
    "ID" int   NOT NULL,
    "Name" nvarchar(100)   NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY (
        "ID"
     ),
    CONSTRAINT "uc_Genre_Name" UNIQUE (
        "Name"
    )
);

CREATE TABLE "GenreInFilm" (
    "ID" int   NOT NULL,
    "FilmID" int   NOT NULL,
    "GenreID" int   NOT NULL,
    CONSTRAINT "pk_GenreInFilm" PRIMARY KEY (
        "ID"
     )
);

CREATE TABLE "User" (
    "ID" int   NOT NULL,
    "Email" varchar(255)   NOT NULL,
    "Login" varchar(255)   NOT NULL,
    "Name" varchar(255)   NOT NULL,
    "Birthday" date   NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY (
        "ID"
     ),
    CONSTRAINT "uc_User_Email" UNIQUE (
        "Email"
    ),
    CONSTRAINT "uc_User_Login" UNIQUE (
        "Login"
    )
);

CREATE TABLE "UserFilmLike" (
    "ID" int   NOT NULL,
    "UserID" int   NOT NULL,
    "FilmID" int   NOT NULL,
    CONSTRAINT "pk_UserFilmLike" PRIMARY KEY (
        "ID"
     )
);

CREATE TABLE "UserFriend" (
    "ID" int   NOT NULL,
    "UserID" int   NOT NULL,
    "FriendID" int   NOT NULL,
    "Status" int   NOT NULL,
    CONSTRAINT "pk_UserFriend" PRIMARY KEY (
        "ID"
     )
);

CREATE TABLE "FriendStatus" (
    "ID" int   NOT NULL,
    "Name" nvarchar(100)   NOT NULL,
    CONSTRAINT "pk_FriendStatus" PRIMARY KEY (
        "ID"
     ),
    CONSTRAINT "uc_FriendStatus_Name" UNIQUE (
        "Name"
    )
);

ALTER TABLE "Film" ADD CONSTRAINT "fk_Film_MPARatingID" FOREIGN KEY("MPARatingID")
REFERENCES "MPARating" ("ID");

ALTER TABLE "GenreInFilm" ADD CONSTRAINT "fk_GenreInFilm_FilmID" FOREIGN KEY("FilmID")
REFERENCES "Film" ("ID");

ALTER TABLE "GenreInFilm" ADD CONSTRAINT "fk_GenreInFilm_GenreID" FOREIGN KEY("GenreID")
REFERENCES "Genre" ("ID");

ALTER TABLE "UserFilmLike" ADD CONSTRAINT "fk_UserFilmLike_UserID" FOREIGN KEY("UserID")
REFERENCES "User" ("ID");

ALTER TABLE "UserFilmLike" ADD CONSTRAINT "fk_UserFilmLike_FilmID" FOREIGN KEY("FilmID")
REFERENCES "Film" ("ID");

ALTER TABLE "UserFriend" ADD CONSTRAINT "fk_UserFriend_UserID" FOREIGN KEY("UserID")
REFERENCES "User" ("ID");

ALTER TABLE "UserFriend" ADD CONSTRAINT "fk_UserFriend_FriendID" FOREIGN KEY("FriendID")
REFERENCES "User" ("ID");

ALTER TABLE "UserFriend" ADD CONSTRAINT "fk_UserFriend_Status" FOREIGN KEY("Status")
REFERENCES "FriendStatus" ("ID");

