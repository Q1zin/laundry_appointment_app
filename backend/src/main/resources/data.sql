-- Insert test users
-- Password for all users: "password123"
-- BCrypt hash generated with BCrypt strength 10
INSERT INTO users (id, name, password_hash, role, is_blocked) VALUES
('user-1', 'admin', '$2a$10$iu2zg0zoMgtbJvwq6gHGXuhpkQ35Mpyfg.7uSUIbaL/j/9SotxWHi', 'admin', false),
('user-2', 'john_doe', '$2a$10$iu2zg0zoMgtbJvwq6gHGXuhpkQ35Mpyfg.7uSUIbaL/j/9SotxWHi', 'user', false),
('user-3', 'jane_smith', '$2a$10$iu2zg0zoMgtbJvwq6gHGXuhpkQ35Mpyfg.7uSUIbaL/j/9SotxWHi', 'user', false),
('user-4', 'blocked_user', '$2a$10$iu2zg0zoMgtbJvwq6gHGXuhpkQ35Mpyfg.7uSUIbaL/j/9SotxWHi', 'user', true);

-- Insert machines
INSERT INTO machines (id, name, status) VALUES
('machine-1', 'Стиральная машина #1', 'available'),
('machine-2', 'Стиральная машина #2', 'available'),
('machine-3', 'Стиральная машина #3', 'available'),
('machine-4', 'Стиральная машина #4', 'blocked');

-- Insert timeslots for today and next 7 days
-- Generate slots from 08:00 to 22:00 with 2-hour intervals
INSERT INTO timeslots (id, start_time, end_time, is_available, machine_id)
SELECT 
    gen_random_uuid()::text,
    (CURRENT_DATE + days.day_offset) + (INTERVAL '1 hour' * (8 + hours.hour_offset * 2)),
    (CURRENT_DATE + days.day_offset) + (INTERVAL '1 hour' * (10 + hours.hour_offset * 2)),
    true,
    m.id
FROM 
    machines m,
    generate_series(0, 7) AS days(day_offset),
    generate_series(0, 6) AS hours(hour_offset)
WHERE 
    m.status = 'available';

-- Insert sample schedules for next 7 days
INSERT INTO schedules (id, date, is_open)
SELECT 
    gen_random_uuid()::text,
    CURRENT_DATE + day_offset,
    CASE WHEN day_offset <= 2 THEN true ELSE false END
FROM 
    generate_series(0, 7) AS day_offset;

-- Insert sample bookings
INSERT INTO bookings (id, user_id, machine_id, slot_id, state)
SELECT
    gen_random_uuid()::text,
    'user-2',
    machine_id,
    id,
    'active'
FROM timeslots
WHERE DATE(start_time) = CURRENT_DATE
AND machine_id = 'machine-1'
LIMIT 1;

INSERT INTO bookings (id, user_id, machine_id, slot_id, state)
SELECT
    gen_random_uuid()::text,
    'user-3',
    machine_id,
    id,
    'active'
FROM timeslots
WHERE DATE(start_time) = CURRENT_DATE + 1
AND machine_id = 'machine-2'
LIMIT 1;

-- Mark booked slots as unavailable
UPDATE timeslots
SET is_available = false
WHERE id IN (
    SELECT slot_id FROM bookings WHERE state = 'active'
);
