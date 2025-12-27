┌────────────────────────┐
│       COUNSELLOR       │
├────────────────────────┤
│ PK  id                 │
│     name               │
│     email (UNIQUE)     │
│     password           │
│     phnum              │
│     created_at         │
│     updated_at         │
└───────────┬────────────┘
            │ 1
            │
            │ creates
            │
            │ many
            ▼
┌────────────────────────┐
│        ENQUIRY         │
├────────────────────────┤
│ PK  id                 │
│     student_name       │
│     phno               │
│ FK  classmode_id       │
│ FK  course_id          │
│ FK  status_id          │
│ FK  counsellor_id_fk   │
│     created_at         │
│     updated_at         │
└─────┬─────────┬────────┘
      │         │
      │         │
      ▼         ▼
┌──────────────┐   ┌──────────────┐
│  CLASSMODE   │   │    COURSE    │
├──────────────┤   ├──────────────┤
│ PK  id       │   │ PK  id       │
│     mode_name│   │     course_name│
└──────────────┘   └──────────────┘
      │
      │
      ▼
┌──────────────┐
│    STATUS    │
├──────────────┤
│ PK  id       │
│     status_name│
└──────────────┘


CREATE TABLE COUNSELLOR (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    phnum VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE CLASSMODE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mode_name VARCHAR(50) UNIQUE
);

CREATE TABLE COURSE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) UNIQUE
);

CREATE TABLE STATUS (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(50) UNIQUE
);

CREATE TABLE ENQUIRY (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(255),
    phno VARCHAR(20),
    classmode_id INT,
    course_id INT,
    status_id INT,
    counsellor_id_fk INT,
    -- Automatically set to current time on insert
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Automatically set to current time on insert AND update
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (classmode_id) REFERENCES CLASSMODE(id),
    FOREIGN KEY (course_id) REFERENCES COURSE(id),
    FOREIGN KEY (status_id) REFERENCES STATUS(id),
    FOREIGN KEY (counsellor_id_fk) REFERENCES COUNSELLOR(id)
);