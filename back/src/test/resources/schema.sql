-- ============================================
-- Database schema for testing
-- ============================================ 
-- This file creates the necessary tables in H2
-- Compatible with the production MySQL schema

-- TEACHERS table
CREATE TABLE IF NOT EXISTS TEACHERS (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(20),
    first_name VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- USERS table
CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(20),
    first_name VARCHAR(20),
    admin BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(50) UNIQUE,
    password VARCHAR(120),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- SESSIONS table
CREATE TABLE IF NOT EXISTS SESSIONS (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description VARCHAR(2500),
    date TIMESTAMP NOT NULL,
    teacher_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES TEACHERS(id)
);

-- PARTICIPATE table (many-to-many)
CREATE TABLE IF NOT EXISTS PARTICIPATE (
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES SESSIONS(id),
    FOREIGN KEY (user_id) REFERENCES USERS(id)
);
