-- ============================================
-- Test data for integration tests
-- ============================================
-- Consistent data set for all tests

-- Teachers
INSERT INTO TEACHERS (first_name, last_name) VALUES ('Margot', 'DELAHAYE');
INSERT INTO TEACHERS (first_name, last_name) VALUES ('Hélène', 'THIERCELIN');

-- Users (passwords are BCrypt hashed "test!1234")
INSERT INTO USERS (first_name, last_name, admin, email, password) VALUES ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');
INSERT INTO USERS (first_name, last_name, admin, email, password) VALUES ('User', 'User', false, 'user@test.com', '$2a$10$UVoAC3F3ksugfpByLsLWxuGwQrTJU08tJ8jWr6gBs7uetpQfpI4rS');

-- Sessions
INSERT INTO SESSIONS (name, description, teacher_id, date) VALUES ('Beginners Yoga', 'Session for beginners', 1, '2026-01-01 12:00:00');
INSERT INTO SESSIONS (name, description, teacher_id, date) VALUES ('Advanced Yoga', 'Session for advanced practitioners', 2, '2026-01-01 16:00:00');