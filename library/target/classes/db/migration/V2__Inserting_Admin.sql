-- Insert initial admin user
-- Decoded password is admin123
INSERT INTO users (username, password, email)
VALUES ('admin', '$2a$10$3LmmBvtOdH0OHLLTxOdbc.iw14A24pJHwIRwJFjGX1jX1rjl6OSgS', 'admin@example.com');

-- Add admin role to the admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, role r WHERE u.username='admin' AND r.name='ROLE_ADMIN';
