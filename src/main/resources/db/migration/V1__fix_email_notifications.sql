-- Fix null values in email_notifications_enabled column
UPDATE users SET email_notifications_enabled = TRUE WHERE email_notifications_enabled IS NULL;

-- Ensure column has default value
ALTER TABLE users MODIFY COLUMN email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE;