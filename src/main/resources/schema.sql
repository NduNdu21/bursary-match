-- Bursary Match System schema (PostgreSQL)
-- Run this once against your Neon/Supabase database before starting the app.

CREATE TABLE IF NOT EXISTS tblStudents (
    student_id     VARCHAR(20) PRIMARY KEY,
    username       VARCHAR(50) UNIQUE NOT NULL,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100) NOT NULL,
    surname        VARCHAR(100) NOT NULL,
    date_of_birth  DATE NOT NULL,
    school         VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS tblStudentMarks (
    student_id      VARCHAR(20) PRIMARY KEY REFERENCES tblStudents(student_id),
    home_lang       VARCHAR(50),
    hl_mark         INT,
    first_add_lang  VARCHAR(50),
    fal_mark        INT,
    math_subject    VARCHAR(50),
    math_mark       INT,
    sub4            VARCHAR(50),
    sub4_mark       INT,
    sub5            VARCHAR(50),
    sub5_mark       INT,
    sub6            VARCHAR(50),
    sub6_mark       INT,
    lo_mark         INT
);

CREATE TABLE IF NOT EXISTS tblBursaryProviders (
    provider_id    VARCHAR(20) PRIMARY KEY,
    username       VARCHAR(50) UNIQUE NOT NULL,
    password       VARCHAR(255) NOT NULL,
    provider_name  VARCHAR(150) NOT NULL,
    biography      TEXT,
    phone_number   VARCHAR(20),
    email          VARCHAR(100),
    website        VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS tblOffers (
    offer_id           SERIAL PRIMARY KEY,
    offer_name         VARCHAR(150) NOT NULL,
    additional_info    TEXT,
    aps_required       INT,
    avg_required       INT,
    hl_required        INT,
    fal_required       INT,
    math_required      INT,
    sub4_required      INT,
    sub5_required      INT,
    sub6_required      INT,
    faculty            VARCHAR(100),
    deadline           DATE,
    num_bursaries_left INT,
    provider_id        VARCHAR(20) REFERENCES tblBursaryProviders(provider_id)
);
