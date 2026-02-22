-- ============================================================
-- V10: Comprehensive seed data for all tables
-- ============================================================
-- Passwords are BCrypt hash of 'Password1!'
-- $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- ============================================================

-- ============================================================
-- USERS
-- ============================================================
INSERT INTO users (id, user_email, user_password, user_enabled, created_date, created_by)
VALUES
-- Admins
('u-admin-001', 'admin@fithub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '180 days', 'SYSTEM'),
-- Trainers
('u-trainer-01', 'oleksiy.koval@fithub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '150 days', 'SYSTEM'),
('u-trainer-02', 'iryna.melnyk@fithub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '140 days', 'SYSTEM'),
('u-trainer-03', 'dmytro.bondar@fithub.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '120 days', 'SYSTEM'),
-- Clients
('u-client-001', 'andrii.shevchenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '100 days', 'SYSTEM'),
('u-client-002', 'olha.petrenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '90 days', 'SYSTEM'),
('u-client-003', 'vasyl.kovalenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '85 days', 'SYSTEM'),
('u-client-004', 'natalia.savchenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '75 days', 'SYSTEM'),
('u-client-005', 'mykola.kravchenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '60 days', 'SYSTEM'),
('u-client-006', 'svitlana.moroz@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '50 days', 'SYSTEM'),
('u-client-007', 'ihor.lysenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', FALSE,
 NOW() - INTERVAL '45 days', 'SYSTEM'),
('u-client-008', 'tetiana.bondarenko@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
 NOW() - INTERVAL '30 days', 'SYSTEM');

-- ============================================================
-- USER ROLES
-- ============================================================
INSERT INTO user_roles (user_id, role_id)
VALUES ('u-admin-001', '00000000-0000-0000-0000-000000000001'),
       ('u-trainer-01', '00000000-0000-0000-0000-000000000002'),
       ('u-trainer-02', '00000000-0000-0000-0000-000000000002'),
       ('u-trainer-03', '00000000-0000-0000-0000-000000000002'),
       ('u-client-001', '00000000-0000-0000-0000-000000000003'),
       ('u-client-002', '00000000-0000-0000-0000-000000000003'),
       ('u-client-003', '00000000-0000-0000-0000-000000000003'),
       ('u-client-004', '00000000-0000-0000-0000-000000000003'),
       ('u-client-005', '00000000-0000-0000-0000-000000000003'),
       ('u-client-006', '00000000-0000-0000-0000-000000000003'),
       ('u-client-007', '00000000-0000-0000-0000-000000000003'),
       ('u-client-008', '00000000-0000-0000-0000-000000000003');

-- ============================================================
-- SPECIALIZATIONS
-- ============================================================
INSERT INTO specializations (id, name, description, active)
VALUES ('spec-001', 'Strength Training', 'Powerlifting, Olympic lifting, progressive overload methodology', TRUE),
       ('spec-002', 'Cardio & Endurance', 'Running, cycling, HIIT, cardiovascular health improvement', TRUE),
       ('spec-003', 'Yoga & Flexibility', 'Hatha yoga, stretching, mobility and mindfulness practices', TRUE),
       ('spec-004', 'Weight Loss', 'Fat burning programs, nutrition coaching, body recomposition', TRUE),
       ('spec-005', 'Muscle Hypertrophy', 'Bodybuilding-style training, muscle mass gain techniques', TRUE),
       ('spec-006', 'Rehabilitation', 'Injury recovery, corrective exercise, post-surgery training', TRUE),
       ('spec-007', 'Sports Performance', 'Athletic conditioning, speed, agility and sport-specific drills', TRUE),
       ('spec-008', 'Nutrition Coaching', 'Macro planning, meal prep guidance, dietary analysis', TRUE),
       ('spec-009', 'Senior Fitness', 'Low-impact training for 55+, balance and fall prevention', FALSE);

-- ============================================================
-- TRAINER PROFILES
-- ============================================================
INSERT INTO trainer_profiles (id, first_name, last_name, experience_years, description, active, user_id, created_date,
                              created_by)
VALUES ('tp-001', 'Олексій', 'Коваль', 8,
        'Certified strength coach with 8 years of experience. Specializes in powerlifting and hypertrophy programs. Multiple national champion.',
        TRUE, 'u-trainer-01', NOW() - INTERVAL '150 days', 'SYSTEM'),
       ('tp-002', 'Ірина', 'Мельник', 5,
        'Yoga instructor and cardio specialist. Combines mindfulness with functional fitness. Certified in Hatha and Vinyasa yoga.',
        TRUE, 'u-trainer-02', NOW() - INTERVAL '140 days', 'SYSTEM'),
       ('tp-003', 'Дмитро', 'Бондар', 12,
        'Sports performance coach with background in professional athletics. Works with amateur athletes and competitive bodybuilders.',
        TRUE, 'u-trainer-03', NOW() - INTERVAL '120 days', 'SYSTEM');

INSERT INTO trainer_specialization (trainer_id, specialization_id)
VALUES ('tp-001', 'spec-001'),
       ('tp-001', 'spec-005'),
       ('tp-001', 'spec-004'),
       ('tp-002', 'spec-002'),
       ('tp-002', 'spec-003'),
       ('tp-002', 'spec-004'),
       ('tp-003', 'spec-007'),
       ('tp-003', 'spec-005'),
       ('tp-003', 'spec-008');

-- ============================================================
-- CLIENT PROFILES
-- ============================================================
INSERT INTO client_profiles (id, first_name, last_name, phone, birth_date, height, weight, daily_water_target,
                             client_gender, active, user_id, created_date, created_by)
VALUES ('cp-001', 'Андрій', 'Шевченко', '+380971234501', '1993-04-15', 182.0, 88.5, 3100, 'MALE', TRUE, 'u-client-001',
        NOW() - INTERVAL '100 days', 'SYSTEM'),
       ('cp-002', 'Ольга', 'Петренко', '+380971234502', '1996-08-22', 165.0, 58.0, 2030, 'FEMALE', TRUE, 'u-client-002',
        NOW() - INTERVAL '90 days', 'SYSTEM'),
       ('cp-003', 'Василь', 'Коваленко', '+380971234503', '1988-11-03', 175.0, 95.0, 3325, 'MALE', TRUE, 'u-client-003',
        NOW() - INTERVAL '85 days', 'SYSTEM'),
       ('cp-004', 'Наталія', 'Савченко', '+380971234504', '2000-02-17', 168.0, 62.5, 2188, 'FEMALE', TRUE,
        'u-client-004', NOW() - INTERVAL '75 days', 'SYSTEM'),
       ('cp-005', 'Микола', 'Кравченко', '+380971234505', '1990-06-30', 178.0, 82.0, 2870, 'MALE', TRUE, 'u-client-005',
        NOW() - INTERVAL '60 days', 'SYSTEM'),
       ('cp-006', 'Світлана', 'Мороз', '+380971234506', '1998-12-05', 162.0, 55.0, 1925, 'FEMALE', TRUE, 'u-client-006',
        NOW() - INTERVAL '50 days', 'SYSTEM'),
       ('cp-007', 'Ігор', 'Лисенко', '+380971234507', '1985-09-11', 180.0, 102.0, 3570, 'MALE', FALSE, 'u-client-007',
        NOW() - INTERVAL '45 days', 'SYSTEM'),
       ('cp-008', 'Тетяна', 'Бондаренко', '+380971234508', '2002-03-28', 170.0, 65.0, 2275, 'FEMALE', TRUE,
        'u-client-008', NOW() - INTERVAL '30 days', 'SYSTEM');

-- ============================================================
-- MEMBERSHIPS
-- ============================================================
INSERT INTO memberships (id, membership_type, membership_status, start_date, end_date, visits_left, freeze_date,
                         duration_months, client_id, created_date, created_by)
VALUES
-- Active monthly memberships
('mem-001', 'MONTHLY', 'ACTIVE', NOW() - INTERVAL '60 days', NOW() + INTERVAL '30 days', NULL, NULL, 3, 'cp-001',
 NOW() - INTERVAL '65 days', 'SYSTEM'),
('mem-002', 'MONTHLY', 'ACTIVE', NOW() - INTERVAL '45 days', NOW() + INTERVAL '45 days', NULL, NULL, 3, 'cp-002',
 NOW() - INTERVAL '50 days', 'SYSTEM'),
('mem-003', 'YEARLY', 'ACTIVE', NOW() - INTERVAL '30 days', NOW() + INTERVAL '335 days', NULL, NULL, 12, 'cp-003',
 NOW() - INTERVAL '35 days', 'SYSTEM'),
('mem-004', 'VISITS', 'ACTIVE', NOW() - INTERVAL '20 days', NULL, 8, NULL, NULL, 'cp-004', NOW() - INTERVAL '25 days',
 'SYSTEM'),
('mem-005', 'MONTHLY', 'ACTIVE', NOW() - INTERVAL '15 days', NOW() + INTERVAL '75 days', NULL, NULL, 3, 'cp-005',
 NOW() - INTERVAL '20 days', 'SYSTEM'),
('mem-006', 'VISITS', 'ACTIVE', NOW() - INTERVAL '10 days', NULL, 12, NULL, NULL, 'cp-006', NOW() - INTERVAL '15 days',
 'SYSTEM'),
-- Frozen membership
('mem-007', 'MONTHLY', 'FROZEN', NOW() - INTERVAL '40 days', NOW() + INTERVAL '20 days', NULL,
 NOW() - INTERVAL '5 days', 3, 'cp-007', NOW() - INTERVAL '45 days', 'SYSTEM'),
-- New client — created, not paid yet
('mem-008', 'MONTHLY', 'CREATED', NULL, NULL, NULL, NULL, 1, 'cp-008', NOW() - INTERVAL '5 days', 'SYSTEM'),
-- Historical expired memberships
('mem-h01', 'MONTHLY', 'EXPIRED', NOW() - INTERVAL '120 days', NOW() - INTERVAL '30 days', NULL, NULL, 3, 'cp-001',
 NOW() - INTERVAL '125 days', 'SYSTEM'),
('mem-h02', 'VISITS', 'CANCELLED', NOW() - INTERVAL '80 days', NOW() - INTERVAL '60 days', 0, NULL, NULL, 'cp-002',
 NOW() - INTERVAL '85 days', 'SYSTEM');

-- ============================================================
-- PAYMENTS
-- ============================================================
INSERT INTO payments (id, amount, currency, payment_status, payment_date, client_id, membership_id, created_date,
                      created_by)
VALUES ('pay-001', 1200.00, 'UAH', 'PAID', NOW() - INTERVAL '65 days', 'cp-001', 'mem-001', NOW() - INTERVAL '65 days',
        'SYSTEM'),
       ('pay-002', 900.00, 'UAH', 'PAID', NOW() - INTERVAL '50 days', 'cp-002', 'mem-002', NOW() - INTERVAL '50 days',
        'SYSTEM'),
       ('pay-003', 9600.00, 'UAH', 'PAID', NOW() - INTERVAL '35 days', 'cp-003', 'mem-003', NOW() - INTERVAL '35 days',
        'SYSTEM'),
       ('pay-004', 600.00, 'UAH', 'PAID', NOW() - INTERVAL '25 days', 'cp-004', 'mem-004', NOW() - INTERVAL '25 days',
        'SYSTEM'),
       ('pay-005', 1200.00, 'UAH', 'PAID', NOW() - INTERVAL '20 days', 'cp-005', 'mem-005', NOW() - INTERVAL '20 days',
        'SYSTEM'),
       ('pay-006', 480.00, 'UAH', 'PAID', NOW() - INTERVAL '15 days', 'cp-006', 'mem-006', NOW() - INTERVAL '15 days',
        'SYSTEM'),
-- Historical
       ('pay-h01', 900.00, 'UAH', 'PAID', NOW() - INTERVAL '125 days', 'cp-001', 'mem-h01', NOW() - INTERVAL '125 days',
        'SYSTEM'),
       ('pay-h02', 360.00, 'UAH', 'PAID', NOW() - INTERVAL '85 days', 'cp-002', 'mem-h02', NOW() - INTERVAL '85 days',
        'SYSTEM');

-- ============================================================
-- TRAINING SESSIONS
-- ============================================================
INSERT INTO training_sessions (id, training_type, start_time, end_time, max_participants, training_status, trainer_id,
                               created_date, created_by)
VALUES
-- Scheduled upcoming sessions
('ts-001', 'GROUP', NOW() + INTERVAL '1 day 09:00', NOW() + INTERVAL '1 day 10:00', 15, 'SCHEDULED', 'tp-001',
 NOW() - INTERVAL '10 days', 'tp-001'),
('ts-002', 'GROUP', NOW() + INTERVAL '1 day 18:00', NOW() + INTERVAL '1 day 19:00', 12, 'SCHEDULED', 'tp-002',
 NOW() - INTERVAL '8 days', 'tp-002'),
('ts-003', 'PERSONAL', NOW() + INTERVAL '2 days 10:00', NOW() + INTERVAL '2 days 11:00', 1, 'SCHEDULED', 'tp-001',
 NOW() - INTERVAL '5 days', 'tp-001'),
('ts-004', 'GROUP', NOW() + INTERVAL '3 days 07:00', NOW() + INTERVAL '3 days 08:00', 20, 'SCHEDULED', 'tp-002',
 NOW() - INTERVAL '3 days', 'tp-002'),
('ts-005', 'GROUP', NOW() + INTERVAL '4 days 17:00', NOW() + INTERVAL '4 days 18:30', 10, 'SCHEDULED', 'tp-003',
 NOW() - INTERVAL '2 days', 'tp-003'),
('ts-006', 'PERSONAL', NOW() + INTERVAL '5 days 11:00', NOW() + INTERVAL '5 days 12:00', 1, 'SCHEDULED', 'tp-003',
 NOW() - INTERVAL '1 day', 'tp-003'),
-- Completed sessions
('ts-c01', 'GROUP', NOW() - INTERVAL '7 days 09:00', NOW() - INTERVAL '7 days 10:00', 15, 'COMPLETED', 'tp-001',
 NOW() - INTERVAL '20 days', 'tp-001'),
('ts-c02', 'GROUP', NOW() - INTERVAL '5 days 18:00', NOW() - INTERVAL '5 days 19:00', 12, 'COMPLETED', 'tp-002',
 NOW() - INTERVAL '20 days', 'tp-002'),
('ts-c03', 'PERSONAL', NOW() - INTERVAL '3 days 10:00', NOW() - INTERVAL '3 days 11:00', 1, 'COMPLETED', 'tp-001',
 NOW() - INTERVAL '15 days', 'tp-001'),
('ts-c04', 'GROUP', NOW() - INTERVAL '2 days 07:00', NOW() - INTERVAL '2 days 08:00', 20, 'COMPLETED', 'tp-003',
 NOW() - INTERVAL '10 days', 'tp-003'),
('ts-c05', 'GROUP', NOW() - INTERVAL '10 days 18:00', NOW() - INTERVAL '10 days 19:00', 15, 'COMPLETED', 'tp-001',
 NOW() - INTERVAL '25 days', 'tp-001'),
-- Cancelled
('ts-x01', 'GROUP', NOW() - INTERVAL '1 day 09:00', NOW() - INTERVAL '1 day 10:00', 15, 'CANCELLED', 'tp-002',
 NOW() - INTERVAL '12 days', 'tp-002');

-- Clients joined upcoming sessions
INSERT INTO training_client (training_id, client_id)
VALUES ('ts-001', 'cp-001'),
       ('ts-001', 'cp-003'),
       ('ts-001', 'cp-005'),
       ('ts-002', 'cp-002'),
       ('ts-002', 'cp-004'),
       ('ts-002', 'cp-006'),
       ('ts-003', 'cp-001'),
       ('ts-004', 'cp-002'),
       ('ts-004', 'cp-004'),
       ('ts-004', 'cp-005'),
       ('ts-005', 'cp-003'),
       ('ts-005', 'cp-005'),
       ('ts-c01', 'cp-001'),
       ('ts-c01', 'cp-003'),
       ('ts-c01', 'cp-005'),
       ('ts-c02', 'cp-002'),
       ('ts-c02', 'cp-004'),
       ('ts-c03', 'cp-001'),
       ('ts-c04', 'cp-003'),
       ('ts-c04', 'cp-005'),
       ('ts-c05', 'cp-001'),
       ('ts-c05', 'cp-002'),
       ('ts-c05', 'cp-003');

-- ============================================================
-- ATTENDANCES
-- ============================================================
INSERT INTO attendances (id, check_in_time, client_id, session_id, created_date, created_by)
VALUES ('att-001', NOW() - INTERVAL '7 days 09:05', 'cp-001', 'ts-c01', NOW() - INTERVAL '7 days', 'tp-001'),
       ('att-002', NOW() - INTERVAL '7 days 09:10', 'cp-003', 'ts-c01', NOW() - INTERVAL '7 days', 'tp-001'),
       ('att-003', NOW() - INTERVAL '7 days 09:08', 'cp-005', 'ts-c01', NOW() - INTERVAL '7 days', 'tp-001'),
       ('att-004', NOW() - INTERVAL '5 days 18:03', 'cp-002', 'ts-c02', NOW() - INTERVAL '5 days', 'tp-002'),
       ('att-005', NOW() - INTERVAL '5 days 18:07', 'cp-004', 'ts-c02', NOW() - INTERVAL '5 days', 'tp-002'),
       ('att-006', NOW() - INTERVAL '3 days 10:01', 'cp-001', 'ts-c03', NOW() - INTERVAL '3 days', 'tp-001'),
       ('att-007', NOW() - INTERVAL '2 days 07:04', 'cp-003', 'ts-c04', NOW() - INTERVAL '2 days', 'tp-003'),
       ('att-008', NOW() - INTERVAL '2 days 07:06', 'cp-005', 'ts-c04', NOW() - INTERVAL '2 days', 'tp-003'),
       ('att-009', NOW() - INTERVAL '10 days 18:02', 'cp-001', 'ts-c05', NOW() - INTERVAL '10 days', 'tp-001'),
       ('att-010', NOW() - INTERVAL '10 days 18:05', 'cp-002', 'ts-c05', NOW() - INTERVAL '10 days', 'tp-001'),
       ('att-011', NOW() - INTERVAL '10 days 18:09', 'cp-003', 'ts-c05', NOW() - INTERVAL '10 days', 'tp-001');

-- ============================================================
-- EXERCISES
-- ============================================================
INSERT INTO exercises (id, name, description, category, primary_muscle_group, video_url, instructions, active,
                       created_date, created_by)
VALUES ('ex-001', 'Barbell Back Squat', 'Classic compound lower body exercise', 'STRENGTH', 'QUADRICEPS',
        'https://example.com/squat.mp4',
        'Stand with bar on upper back. Descend until thighs are parallel. Drive through heels to return.', TRUE,
        NOW() - INTERVAL '100 days', 'u-admin-001'),
       ('ex-002', 'Deadlift', 'King of all compound movements', 'STRENGTH', 'HAMSTRINGS',
        'https://example.com/deadlift.mp4',
        'Hinge at hips, grip bar shoulder-width. Drive hips forward and lift. Control the descent.', TRUE,
        NOW() - INTERVAL '100 days', 'u-admin-001'),
       ('ex-003', 'Bench Press', 'Primary chest pressing movement', 'STRENGTH', 'CHEST',
        'https://example.com/bench.mp4', 'Lie on bench, lower bar to chest, press explosively to lockout.', TRUE,
        NOW() - INTERVAL '100 days', 'u-admin-001'),
       ('ex-004', 'Pull-Up', 'Bodyweight back and bicep exercise', 'STRENGTH', 'BACK', 'https://example.com/pullup.mp4',
        'Hang from bar, pull chest to bar, lower controlled.', TRUE, NOW() - INTERVAL '100 days', 'u-admin-001'),
       ('ex-005', 'Overhead Press', 'Vertical pressing movement for shoulders', 'STRENGTH', 'SHOULDERS',
        'https://example.com/ohp.mp4', 'Press barbell from shoulder height to full arm extension overhead.', TRUE,
        NOW() - INTERVAL '100 days', 'u-admin-001'),
       ('ex-006', 'Romanian Deadlift', 'Hip hinge movement targeting hamstrings', 'STRENGTH', 'HAMSTRINGS', NULL,
        'Keep bar close to legs, hinge until stretch in hamstrings, return to standing.', TRUE,
        NOW() - INTERVAL '95 days', 'u-admin-001'),
       ('ex-007', 'Dumbbell Lunges', 'Unilateral lower body exercise', 'STRENGTH', 'QUADRICEPS', NULL,
        'Step forward into lunge, knee above ankle, push back to start.', TRUE, NOW() - INTERVAL '95 days',
        'u-admin-001'),
       ('ex-008', 'Dumbbell Row', 'Unilateral back exercise', 'STRENGTH', 'BACK', NULL,
        'Support one hand on bench, row dumbbell to hip, control down.', TRUE, NOW() - INTERVAL '95 days',
        'u-admin-001'),
       ('ex-009', 'Incline Dumbbell Press', 'Upper chest focused pressing', 'STRENGTH', 'CHEST', NULL,
        'Press dumbbells at 30-45 degree incline angle, controlled tempo.', TRUE, NOW() - INTERVAL '90 days',
        'u-admin-001'),
       ('ex-010', 'Leg Press', 'Machine-based quad and glute development', 'STRENGTH', 'QUADRICEPS', NULL,
        'Push platform away until legs nearly straight, lower controlled.', TRUE, NOW() - INTERVAL '90 days',
        'u-admin-001'),
       ('ex-011', 'Treadmill Run', 'Cardio endurance machine exercise', 'CARDIO', 'FULL_BODY', NULL,
        'Maintain consistent pace, engage core, land midfoot.', TRUE, NOW() - INTERVAL '90 days', 'u-admin-001'),
       ('ex-012', 'Jump Rope', 'High intensity cardio exercise', 'CARDIO', 'CALVES', NULL,
        'Keep elbows close to body, rotate wrists, light bounces.', TRUE, NOW() - INTERVAL '90 days', 'u-admin-001'),
       ('ex-013', 'Burpee', 'Full-body high intensity movement', 'CARDIO', 'FULL_BODY', NULL,
        'Squat, kick back to plank, push-up, jump feet in, explosive jump.', TRUE, NOW() - INTERVAL '88 days',
        'u-admin-001'),
       ('ex-014', 'Plank', 'Core stability isometric exercise', 'CORE', 'ABS', NULL,
        'Hold straight body position on forearms, brace core, breathe steadily.', TRUE, NOW() - INTERVAL '88 days',
        'u-admin-001'),
       ('ex-015', 'Cable Crunch', 'Weighted core flexion exercise', 'CORE', 'ABS', NULL,
        'Kneel before cable, pull weight down with crunch motion.', TRUE, NOW() - INTERVAL '85 days', 'u-admin-001'),
       ('ex-016', 'Dumbbell Bicep Curl', 'Isolation exercise for biceps', 'STRENGTH', 'BICEPS', NULL,
        'Curl dumbbells from full extension to full contraction.', TRUE, NOW() - INTERVAL '85 days', 'u-admin-001'),
       ('ex-017', 'Tricep Pushdown', 'Cable isolation for triceps', 'STRENGTH', 'TRICEPS', NULL,
        'Push cable attachment down to full extension, controlled return.', TRUE, NOW() - INTERVAL '85 days',
        'u-admin-001'),
       ('ex-018', 'Calf Raise', 'Isolation exercise for calves', 'STRENGTH', 'CALVES', NULL,
        'Rise onto tiptoes, hold, lower below platform for full stretch.', TRUE, NOW() - INTERVAL '82 days',
        'u-admin-001'),
       ('ex-019', 'Face Pull', 'Rear delt and upper back health exercise', 'STRENGTH', 'SHOULDERS', NULL,
        'Pull cable to face level, elbows high, external rotation at end.', TRUE, NOW() - INTERVAL '80 days',
        'u-admin-001'),
       ('ex-020', 'Hip Thrust', 'Glute-dominant compound exercise', 'STRENGTH', 'GLUTES', NULL,
        'Upper back on bench, drive hips up with barbell, squeeze glutes at top.', TRUE, NOW() - INTERVAL '80 days',
        'u-admin-001');

-- Secondary muscles
INSERT INTO exercise_secondary_muscles (exercise_id, muscle_group)
VALUES ('ex-001', 'HAMSTRINGS'),
       ('ex-001', 'GLUTES'),
       ('ex-001', 'CORE'),
       ('ex-002', 'QUADRICEPS'),
       ('ex-002', 'GLUTES'),
       ('ex-002', 'BACK'),
       ('ex-002', 'CORE'),
       ('ex-003', 'TRICEPS'),
       ('ex-003', 'SHOULDERS'),
       ('ex-004', 'BICEPS'),
       ('ex-004', 'CORE'),
       ('ex-005', 'TRICEPS'),
       ('ex-005', 'CORE'),
       ('ex-006', 'GLUTES'),
       ('ex-006', 'BACK'),
       ('ex-007', 'HAMSTRINGS'),
       ('ex-007', 'GLUTES'),
       ('ex-008', 'BICEPS'),
       ('ex-008', 'SHOULDERS'),
       ('ex-020', 'HAMSTRINGS'),
       ('ex-020', 'CORE');

-- ============================================================
-- WORKOUT PLANS
-- ============================================================
INSERT INTO workout_plans (id, name, description, difficulty_level, duration_weeks, sessions_per_week, active,
                           trainer_id, created_date, created_by)
VALUES ('wp-001', 'Beginner Strength Foundation',
        'Perfect starting point for novice lifters. Focus on mastering squat, bench and deadlift.', 'BEGINNER', 8, 3,
        TRUE, 'tp-001', NOW() - INTERVAL '90 days', 'u-trainer-01'),
       ('wp-002', 'Intermediate Hypertrophy Block',
        'Upper/lower split targeting muscle hypertrophy through progressive overload and volume.', 'INTERMEDIATE', 10,
        4, TRUE, 'tp-001', NOW() - INTERVAL '80 days', 'u-trainer-01'),
       ('wp-003', 'Advanced Powerlifting Prep',
        '12-week peaking cycle for competitive powerlifters. Includes percentage-based programming.', 'ADVANCED', 12, 5,
        TRUE, 'tp-001', NOW() - INTERVAL '70 days', 'u-trainer-01'),
       ('wp-004', 'Cardio & Yoga Fusion',
        'Blend of cardiovascular conditioning with yoga flexibility work. Ideal for general wellness.', 'BEGINNER', 6,
        3, TRUE, 'tp-002', NOW() - INTERVAL '85 days', 'u-trainer-02'),
       ('wp-005', 'HIIT Shred Program',
        'High-intensity interval training focused on fat loss. 20-30 min sessions with maximum effort.', 'INTERMEDIATE',
        8, 4, TRUE, 'tp-002', NOW() - INTERVAL '75 days', 'u-trainer-02'),
       ('wp-006', 'Athletic Performance Protocol',
        'Sport-specific conditioning program for athletes. Speed, power, agility and endurance combined.', 'ADVANCED',
        12, 5, TRUE, 'tp-003', NOW() - INTERVAL '80 days', 'u-trainer-03'),
       ('wp-007', 'Body Recomposition 12-Week',
        'Simultaneous fat loss and muscle gain through strategic nutrition and training periodization.', 'INTERMEDIATE',
        12, 4, TRUE, 'tp-003', NOW() - INTERVAL '60 days', 'u-trainer-03'),
       ('wp-008', 'Deprecated Old Plan', 'Outdated plan replaced by newer version.', 'BEGINNER', 4, 3, FALSE, 'tp-001',
        NOW() - INTERVAL '110 days', 'u-trainer-01');

-- ============================================================
-- WORKOUT PLAN EXERCISES
-- ============================================================
-- wp-001: Beginner Strength (3 days: Day1=Squat focus, Day2=Press focus, Day3=Dead focus)
INSERT INTO workout_plan_exercises (id, workout_plan_id, exercise_id, day_number, order_index, sets, reps, rest_seconds,
                                    notes, created_date, created_by)
VALUES ('wpe-001', 'wp-001', 'ex-001', 1, 0, 3, 5, 180, 'Focus on form, add 2.5kg each session',
        NOW() - INTERVAL '90 days', 'u-trainer-01'),
       ('wpe-002', 'wp-001', 'ex-006', 1, 1, 3, 8, 120, 'Control the descent', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-003', 'wp-001', 'ex-007', 1, 2, 3, 10, 90, 'Keep torso upright', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-004', 'wp-001', 'ex-003', 2, 0, 3, 5, 180, 'Touch chest, full lockout', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-005', 'wp-001', 'ex-004', 2, 1, 3, 6, 120, 'Use band if needed', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-006', 'wp-001', 'ex-005', 2, 2, 3, 5, 150, 'Press from shoulder rack', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-007', 'wp-001', 'ex-002', 3, 0, 1, 5, 300, 'Work up to 1 heavy set', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-008', 'wp-001', 'ex-008', 3, 1, 3, 8, 120, 'Full stretch at bottom', NOW() - INTERVAL '90 days',
        'u-trainer-01'),
       ('wpe-009', 'wp-001', 'ex-014', 3, 2, 3, NULL, 60, 'Hold 30-45 seconds per set', NOW() - INTERVAL '90 days',
        'u-trainer-01');

-- wp-002: Intermediate Hypertrophy (Day1=UpperA, Day2=LowerA)
INSERT INTO workout_plan_exercises (id, workout_plan_id, exercise_id, day_number, order_index, sets, reps, rest_seconds,
                                    notes, created_date, created_by)
VALUES ('wpe-010', 'wp-002', 'ex-003', 1, 0, 4, 8, 120, 'RPE 8, controlled tempo', NOW() - INTERVAL '80 days',
        'u-trainer-01'),
       ('wpe-011', 'wp-002', 'ex-009', 1, 1, 3, 10, 90, 'Squeeze at top', NOW() - INTERVAL '80 days', 'u-trainer-01'),
       ('wpe-012', 'wp-002', 'ex-004', 1, 2, 4, 8, 120, 'Full ROM', NOW() - INTERVAL '80 days', 'u-trainer-01'),
       ('wpe-013', 'wp-002', 'ex-005', 1, 3, 3, 10, 90, 'No momentum', NOW() - INTERVAL '80 days', 'u-trainer-01'),
       ('wpe-014', 'wp-002', 'ex-016', 1, 4, 3, 12, 60, 'Superset with tricep', NOW() - INTERVAL '80 days',
        'u-trainer-01'),
       ('wpe-015', 'wp-002', 'ex-001', 2, 0, 4, 8, 150, '3-1-1 tempo', NOW() - INTERVAL '80 days', 'u-trainer-01'),
       ('wpe-016', 'wp-002', 'ex-010', 2, 1, 3, 12, 90, 'High foot placement', NOW() - INTERVAL '80 days',
        'u-trainer-01'),
       ('wpe-017', 'wp-002', 'ex-006', 2, 2, 3, 10, 90, 'Slight knee bend only', NOW() - INTERVAL '80 days',
        'u-trainer-01'),
       ('wpe-018', 'wp-002', 'ex-020', 2, 3, 3, 12, 90, 'Full hip extension at top', NOW() - INTERVAL '80 days',
        'u-trainer-01');

-- ============================================================
-- CLIENT WORKOUT PLAN ASSIGNMENTS
-- ============================================================
INSERT INTO client_workout_plans (id, client_id, workout_plan_id, assigned_date, start_date, end_date, status,
                                  completion_percentage, created_date, created_by)
VALUES ('cwp-001', 'cp-001', 'wp-002', NOW() - INTERVAL '45 days', NOW() - INTERVAL '40 days', NULL, 'IN_PROGRESS',
        42.0, NOW() - INTERVAL '45 days', 'u-trainer-01'),
       ('cwp-002', 'cp-002', 'wp-004', NOW() - INTERVAL '35 days', NOW() - INTERVAL '30 days', NULL, 'IN_PROGRESS',
        55.0, NOW() - INTERVAL '35 days', 'u-trainer-02'),
       ('cwp-003', 'cp-003', 'wp-002', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NULL, 'IN_PROGRESS',
        28.0, NOW() - INTERVAL '30 days', 'u-trainer-01'),
       ('cwp-004', 'cp-004', 'wp-005', NOW() - INTERVAL '20 days', NOW() - INTERVAL '15 days', NULL, 'IN_PROGRESS',
        20.0, NOW() - INTERVAL '20 days', 'u-trainer-02'),
       ('cwp-005', 'cp-005', 'wp-007', NOW() - INTERVAL '10 days', NOW() - INTERVAL '8 days', NULL, 'IN_PROGRESS', 8.0,
        NOW() - INTERVAL '10 days', 'u-trainer-03'),
       ('cwp-006', 'cp-006', 'wp-005', NOW() - INTERVAL '5 days', NULL, NULL, 'ASSIGNED', 0.0,
        NOW() - INTERVAL '5 days', 'u-trainer-02'),
-- Completed
       ('cwp-h01', 'cp-001', 'wp-001', NOW() - INTERVAL '90 days', NOW() - INTERVAL '85 days',
        NOW() - INTERVAL '45 days', 'COMPLETED', 100.0, NOW() - INTERVAL '90 days', 'u-trainer-01'),
-- Cancelled
       ('cwp-h02', 'cp-003', 'wp-006', NOW() - INTERVAL '50 days', NOW() - INTERVAL '45 days',
        NOW() - INTERVAL '35 days', 'CANCELLED', 22.0, NOW() - INTERVAL '50 days', 'u-trainer-03');

-- ============================================================
-- WORKOUT LOGS
-- ============================================================
INSERT INTO workout_logs (id, client_workout_plan_id, exercise_id, workout_date, sets_completed, reps_completed,
                          weight_used, duration_seconds, notes, difficulty_rating, created_date, created_by)
VALUES
-- cp-001 logs for cwp-001 (Intermediate Hypertrophy)
('wl-001', 'cwp-001', 'ex-003', NOW() - INTERVAL '39 days', 4, 8, 80.0, NULL, 'Felt strong, good form throughout', 7,
 NOW() - INTERVAL '39 days', 'u-client-001'),
('wl-002', 'cwp-001', 'ex-004', NOW() - INTERVAL '39 days', 4, 7, NULL, NULL, 'Last set incomplete, need more work', 8,
 NOW() - INTERVAL '39 days', 'u-client-001'),
('wl-003', 'cwp-001', 'ex-001', NOW() - INTERVAL '37 days', 4, 8, 100.0, NULL, 'Good depth, slight fatigue at end', 7,
 NOW() - INTERVAL '37 days', 'u-client-001'),
('wl-004', 'cwp-001', 'ex-003', NOW() - INTERVAL '32 days', 4, 8, 82.5, NULL, 'Progressive overload +2.5kg', 7,
 NOW() - INTERVAL '32 days', 'u-client-001'),
('wl-005', 'cwp-001', 'ex-001', NOW() - INTERVAL '30 days', 4, 8, 102.5, NULL, 'Hips rising too fast on 3rd set', 8,
 NOW() - INTERVAL '30 days', 'u-client-001'),
-- cp-002 logs for cwp-002
('wl-006', 'cwp-002', 'ex-011', NOW() - INTERVAL '28 days', 1, NULL, NULL, 1800, 'Ran 5km in 30min, felt great', 6,
 NOW() - INTERVAL '28 days', 'u-client-002'),
('wl-007', 'cwp-002', 'ex-013', NOW() - INTERVAL '25 days', 4, 10, NULL, NULL, 'Very tough, needed extra rest', 9,
 NOW() - INTERVAL '25 days', 'u-client-002'),
-- cp-003 logs for cwp-003
('wl-008', 'cwp-003', 'ex-003', NOW() - INTERVAL '24 days', 4, 8, 90.0, NULL, 'Good session', 7,
 NOW() - INTERVAL '24 days', 'u-client-003'),
('wl-009', 'cwp-003', 'ex-001', NOW() - INTERVAL '22 days', 4, 8, 120.0, NULL, 'PR depth, very happy', 6,
 NOW() - INTERVAL '22 days', 'u-client-003');

-- ============================================================
-- BODY MEASUREMENTS
-- ============================================================
INSERT INTO body_measurements (id, client_id, measurement_date, weight, body_fat_percentage, muscle_mass, bmi, bmr,
                               body_water_percentage, body_mass, visceral_fat_level, notes, created_date, created_by)
VALUES
-- cp-001 measurements (3 check-ins)
('bm-001', 'cp-001', NOW() - INTERVAL '90 days', 91.0, 18.5, 68.0, 27.4, 2120, 62.0, 3.8, 4, 'Initial assessment',
 NOW() - INTERVAL '90 days', 'u-client-001'),
('bm-002', 'cp-001', NOW() - INTERVAL '45 days', 89.5, 17.8, 68.8, 26.9, 2095, 62.5, 3.7, 4,
 'Good progress after 6 weeks', NOW() - INTERVAL '45 days', 'u-client-001'),
('bm-003', 'cp-001', NOW() - INTERVAL '7 days', 88.5, 16.9, 69.5, 26.7, 2070, 63.2, 3.6, 3,
 'Best results yet, very motivated', NOW() - INTERVAL '7 days', 'u-client-001'),
-- cp-002 measurements
('bm-004', 'cp-002', NOW() - INTERVAL '80 days', 60.5, 25.2, 42.0, 22.2, 1440, 58.0, 2.5, 3, 'Starting baseline',
 NOW() - INTERVAL '80 days', 'u-client-002'),
('bm-005', 'cp-002', NOW() - INTERVAL '40 days', 59.0, 24.1, 42.5, 21.7, 1425, 58.8, 2.4, 3, 'Slow but steady progress',
 NOW() - INTERVAL '40 days', 'u-client-002'),
-- cp-003 measurements
('bm-006', 'cp-003', NOW() - INTERVAL '70 days', 97.0, 22.0, 72.5, 31.7, 2380, 59.0, 4.2, 7,
 'High visceral fat, need cardio', NOW() - INTERVAL '70 days', 'u-client-003'),
('bm-007', 'cp-003', NOW() - INTERVAL '25 days', 95.0, 20.5, 73.2, 31.0, 2340, 60.0, 4.1, 6, 'Improving, cardio added',
 NOW() - INTERVAL '25 days', 'u-client-003'),
-- cp-005 measurements
('bm-008', 'cp-005', NOW() - INTERVAL '55 days', 84.0, 19.5, 63.0, 26.5, 1990, 61.5, 3.5, 5, 'Initial intake',
 NOW() - INTERVAL '55 days', 'u-client-005');

-- ============================================================
-- GOALS
-- ============================================================
INSERT INTO goals (id, client_id, title, description, goal_type, start_value, target_value, current_value, unit,
                   start_date, target_date, completion_date, status, progress_percentage, notes, version, created_date,
                   created_by)
VALUES ('goal-001', 'cp-001', 'Lose 5kg body fat', 'Reduce body fat while maintaining muscle mass', 'WEIGHT', 91.0,
        86.0, 88.5, 'KG', NOW() - INTERVAL '90 days', NOW() + INTERVAL '30 days', NULL, 'ACTIVE', 50.0,
        'Progress going well', 0, NOW() - INTERVAL '90 days', 'u-client-001'),
       ('goal-002', 'cp-001', '100kg Squat 1RM', 'Reach 100kg for a clean single squat', 'STRENGTH', 80.0, 100.0, 102.5,
        'KG', NOW() - INTERVAL '90 days', NOW() + INTERVAL '45 days', NOW() - INTERVAL '5 days', 'COMPLETED', 100.0,
        'Achieved ahead of schedule!', 0, NOW() - INTERVAL '90 days', 'u-client-001'),
       ('goal-003', 'cp-002', 'Run 5km without stopping', 'Build cardio base to run a continuous 5km', 'CARDIO', 0.0,
        5.0, 4.2, 'KM', NOW() - INTERVAL '80 days', NOW() + INTERVAL '20 days', NULL, 'ACTIVE', 84.0, 'Almost there!',
        0, NOW() - INTERVAL '80 days', 'u-client-002'),
       ('goal-004', 'cp-003', 'Reduce visceral fat to 5', 'Bring visceral fat rating down through diet and cardio',
        'BODY_FAT', 7.0, 5.0, 6.0, 'LEVEL', NOW() - INTERVAL '70 days', NOW() + INTERVAL '50 days', NULL, 'ACTIVE',
        50.0, 'Diet improved significantly', 0, NOW() - INTERVAL '70 days', 'u-client-003'),
       ('goal-005', 'cp-004', 'Lose 8kg', 'Weight loss goal through training and nutrition', 'WEIGHT', 62.5, 54.5, 60.0,
        'KG', NOW() - INTERVAL '75 days', NOW() + INTERVAL '90 days', NULL, 'ACTIVE', 30.0, 'Starting to see results',
        0, NOW() - INTERVAL '75 days', 'u-client-004'),
       ('goal-006', 'cp-005', 'Deadlift 140kg', 'Progress deadlift from current 110kg to 140kg', 'STRENGTH', 110.0,
        140.0, 115.0, 'KG', NOW() - INTERVAL '60 days', NOW() + INTERVAL '120 days', NULL, 'ACTIVE', 16.7,
        'Slow start but consistent', 0, NOW() - INTERVAL '60 days', 'u-client-005'),
       ('goal-007', 'cp-006', 'Improve flexibility', 'Achieve full forward fold with flat back', 'FLEXIBILITY', 0.0,
        100.0, 35.0, 'SCORE', NOW() - INTERVAL '50 days', NOW() + INTERVAL '130 days', NULL, 'ACTIVE', 35.0,
        'Yoga sessions paying off', 0, NOW() - INTERVAL '50 days', 'u-client-006');

-- ============================================================
-- PERSONAL RECORDS
-- ============================================================
INSERT INTO personal_records (id, client_id, exercise_id, record_type, value, unit, record_date, previous_record, notes,
                              is_current_best, created_date, created_by)
VALUES ('pr-001', 'cp-001', 'ex-001', 'ONE_REP_MAX', 102.5, 'KG', NOW() - INTERVAL '5 days', 95.0,
        'New PR! Clean depth.', TRUE, NOW() - INTERVAL '5 days', 'u-client-001'),
       ('pr-002', 'cp-001', 'ex-003', 'ONE_REP_MAX', 95.0, 'KG', NOW() - INTERVAL '20 days', 90.0, 'Good lockout.',
        TRUE, NOW() - INTERVAL '20 days', 'u-client-001'),
       ('pr-003', 'cp-001', 'ex-002', 'ONE_REP_MAX', 140.0, 'KG', NOW() - INTERVAL '15 days', 130.0, 'Solid pull.',
        TRUE, NOW() - INTERVAL '15 days', 'u-client-001'),
       ('pr-004', 'cp-003', 'ex-001', 'ONE_REP_MAX', 130.0, 'KG', NOW() - INTERVAL '22 days', NULL, 'First tested max.',
        TRUE, NOW() - INTERVAL '22 days', 'u-client-003'),
       ('pr-005', 'cp-003', 'ex-003', 'ONE_REP_MAX', 105.0, 'KG', NOW() - INTERVAL '18 days', NULL,
        'Comfortable with form.', TRUE, NOW() - INTERVAL '18 days', 'u-client-003'),
       ('pr-006', 'cp-005', 'ex-002', 'ONE_REP_MAX', 115.0, 'KG', NOW() - INTERVAL '30 days', NULL, 'Initial test.',
        TRUE, NOW() - INTERVAL '30 days', 'u-client-005'),
       ('pr-old', 'cp-001', 'ex-001', 'ONE_REP_MAX', 95.0, 'KG', NOW() - INTERVAL '45 days', 87.5,
        'Old record before PR.', FALSE, NOW() - INTERVAL '45 days', 'u-client-001');

-- ============================================================
-- PROGRESS PHOTOS
-- ============================================================
INSERT INTO progress_photos (id, client_id, photo_date, photo_url, angle, notes, measurement_id, created_date,
                             created_by)
VALUES ('pp-001', 'cp-001', NOW() - INTERVAL '90 days', 'https://storage.fithub.com/progress/cp001/front_day0.jpg',
        'FRONT', 'Starting point', 'bm-001', NOW() - INTERVAL '90 days', 'u-client-001'),
       ('pp-002', 'cp-001', NOW() - INTERVAL '90 days', 'https://storage.fithub.com/progress/cp001/side_day0.jpg',
        'SIDE', 'Starting point side', 'bm-001', NOW() - INTERVAL '90 days', 'u-client-001'),
       ('pp-003', 'cp-001', NOW() - INTERVAL '45 days', 'https://storage.fithub.com/progress/cp001/front_day45.jpg',
        'FRONT', '45-day check-in', 'bm-002', NOW() - INTERVAL '45 days', 'u-client-001'),
       ('pp-004', 'cp-001', NOW() - INTERVAL '7 days', 'https://storage.fithub.com/progress/cp001/front_day83.jpg',
        'FRONT', 'Looking lean!', 'bm-003', NOW() - INTERVAL '7 days', 'u-client-001'),
       ('pp-005', 'cp-002', NOW() - INTERVAL '80 days', 'https://storage.fithub.com/progress/cp002/front_day0.jpg',
        'FRONT', 'Baseline photo', 'bm-004', NOW() - INTERVAL '80 days', 'u-client-002'),
       ('pp-006', 'cp-003', NOW() - INTERVAL '70 days', 'https://storage.fithub.com/progress/cp003/front_day0.jpg',
        'FRONT', 'Start of journey', 'bm-006', NOW() - INTERVAL '70 days', 'u-client-003');

-- ============================================================
-- FOODS
-- ============================================================
INSERT INTO foods (id, name, brand, serving_size, serving_unit, calories_per_serving, protein_per_serving,
                   carbs_per_serving, fats_per_serving, fiber_per_serving, sugar_per_serving, barcode, active,
                   created_date, created_by)
VALUES ('food-001', 'Chicken Breast', NULL, 100.0, 'G', 165, 31.0, 0.0, 3.6, 0.0, 0.0, NULL, TRUE,
        NOW() - INTERVAL '80 days', 'u-admin-001'),
       ('food-002', 'Brown Rice', NULL, 100.0, 'G', 111, 2.6, 23.0, 0.9, 1.8, 0.4, NULL, TRUE,
        NOW() - INTERVAL '80 days', 'u-admin-001'),
       ('food-003', 'Broccoli', NULL, 100.0, 'G', 34, 2.8, 6.6, 0.4, 2.6, 1.7, NULL, TRUE, NOW() - INTERVAL '80 days',
        'u-admin-001'),
       ('food-004', 'Whole Eggs', NULL, 1.0, 'PIECE', 72, 6.3, 0.4, 4.8, 0.0, 0.4, NULL, TRUE,
        NOW() - INTERVAL '80 days', 'u-admin-001'),
       ('food-005', 'Greek Yogurt 0%', 'Lactel', 150.0, 'G', 87, 15.0, 7.0, 0.4, 0.0, 6.0, '4823000123001', TRUE,
        NOW() - INTERVAL '79 days', 'u-admin-001'),
       ('food-006', 'Oatmeal', NULL, 100.0, 'G', 389, 17.0, 66.0, 7.0, 10.0, 1.1, NULL, TRUE,
        NOW() - INTERVAL '79 days', 'u-admin-001'),
       ('food-007', 'Whey Protein Chocolate', 'Optimum Nutrition', 30.0, 'G', 120, 24.0, 3.0, 2.5, 0.5, 2.0,
        '0748927029628', TRUE, NOW() - INTERVAL '78 days', 'u-admin-001'),
       ('food-008', 'Sweet Potato', NULL, 100.0, 'G', 86, 1.6, 20.0, 0.1, 3.0, 4.2, NULL, TRUE,
        NOW() - INTERVAL '78 days', 'u-admin-001'),
       ('food-009', 'Salmon', NULL, 100.0, 'G', 208, 20.0, 0.0, 13.0, 0.0, 0.0, NULL, TRUE, NOW() - INTERVAL '77 days',
        'u-admin-001'),
       ('food-010', 'Avocado', NULL, 100.0, 'G', 160, 2.0, 9.0, 15.0, 7.0, 0.7, NULL, TRUE, NOW() - INTERVAL '77 days',
        'u-admin-001'),
       ('food-011', 'Banana', NULL, 1.0, 'PIECE', 89, 1.1, 23.0, 0.3, 2.6, 12.0, NULL, TRUE, NOW() - INTERVAL '76 days',
        'u-admin-001'),
       ('food-012', 'Cottage Cheese', 'President', 100.0, 'G', 98, 11.0, 3.4, 4.3, 0.0, 3.4, '4823000456002', TRUE,
        NOW() - INTERVAL '76 days', 'u-admin-001'),
       ('food-013', 'Olive Oil', NULL, 15.0, 'ML', 119, 0.0, 0.0, 13.5, 0.0, 0.0, NULL, TRUE,
        NOW() - INTERVAL '75 days', 'u-admin-001'),
       ('food-014', 'Almonds', NULL, 30.0, 'G', 174, 6.0, 6.0, 15.0, 3.5, 1.5, NULL, TRUE, NOW() - INTERVAL '75 days',
        'u-admin-001'),
       ('food-015', 'Whole Grain Bread', 'Bimbo', 1.0, 'PIECE', 80, 3.5, 14.0, 1.0, 2.0, 2.0, '8410184032303', TRUE,
        NOW() - INTERVAL '74 days', 'u-admin-001');

-- ============================================================
-- MEAL PLANS
-- ============================================================
INSERT INTO meal_plans (id, client_id, plan_date, total_calories, target_calories, protein, carbs, fats, fiber, sugar,
                        target_protein, target_carbs, target_fats, target_fiber, target_sugar, notes, created_date,
                        created_by)
VALUES ('mp-001', 'cp-001', CURRENT_DATE, 2580, 2600, 205.0, 280.0, 72.0, 28.0, 32.0, 210.0, 280.0, 75.0, 30.0, 35.0,
        'High protein day for training', NOW(), 'u-client-001'),
       ('mp-002', 'cp-001', CURRENT_DATE - INTERVAL '1 day', 2490, 2600, 198.0, 265.0, 74.0, 25.0, 30.0, 210.0, 280.0,
        75.0, 30.0, 35.0, 'Rest day nutrition', NOW() - INTERVAL '1 day', 'u-client-001'),
       ('mp-003', 'cp-002', CURRENT_DATE, 1680, 1700, 128.0, 190.0, 45.0, 22.0, 28.0, 130.0, 190.0, 45.0, 25.0, 30.0,
        'Calorie deficit plan', NOW(), 'u-client-002'),
       ('mp-004', 'cp-003', CURRENT_DATE, 2850, 2800, 230.0, 295.0, 85.0, 32.0, 38.0, 220.0, 290.0, 82.0, 30.0, 36.0,
        'Muscle building phase', NOW(), 'u-client-003');

-- ============================================================
-- MEALS
-- ============================================================
INSERT INTO meals (id, meal_plan_id, meal_type, meal_time, name, description, calories, protein, carbs, fats, fiber,
                   sugar, completed, created_date, created_by)
VALUES
-- cp-001 today meal plan
('meal-001', 'mp-001', 'BREAKFAST', NOW()::DATE + TIME '07:30', 'Oat & Protein Shake', 'Oatmeal with whey and banana',
 480, 38.0, 60.0, 8.0, 6.0, 15.0, TRUE, NOW(), 'u-client-001'),
('meal-002', 'mp-001', 'LUNCH', NOW()::DATE + TIME '12:30', 'Chicken & Rice Bowl', 'Lean protein with complex carbs',
 760, 75.0, 80.0, 14.0, 5.0, 3.0, TRUE, NOW(), 'u-client-001'),
('meal-003', 'mp-001', 'SNACK', NOW()::DATE + TIME '15:30', 'Greek Yogurt & Almonds', 'Protein-rich afternoon snack',
 260, 21.0, 14.0, 16.0, 4.0, 8.0, FALSE, NOW(), 'u-client-001'),
('meal-004', 'mp-001', 'DINNER', NOW()::DATE + TIME '19:00', 'Salmon & Sweet Potato', 'Omega-3 rich dinner', 580, 46.0,
 40.0, 22.0, 6.0, 5.0, FALSE, NOW(), 'u-client-001'),
('meal-005', 'mp-001', 'POST_WORKOUT', NOW()::DATE + TIME '21:00', 'Protein Shake', 'Post-workout recovery', 180, 25.0,
 8.0, 4.0, 1.0, 4.0, FALSE, NOW(), 'u-client-001'),
-- cp-002 today
('meal-006', 'mp-003', 'BREAKFAST', NOW()::DATE + TIME '08:00', 'Egg White Omelette', 'Light high-protein breakfast',
 280, 28.0, 12.0, 8.0, 3.0, 4.0, TRUE, NOW(), 'u-client-002'),
('meal-007', 'mp-003', 'LUNCH', NOW()::DATE + TIME '13:00', 'Tuna Salad', 'Low-calorie lean lunch', 350, 40.0, 20.0,
 10.0, 5.0, 5.0, FALSE, NOW(), 'u-client-002'),
('meal-008', 'mp-003', 'DINNER', NOW()::DATE + TIME '18:30', 'Chicken Salad Bowl', 'Light dinner with veggies', 420,
 38.0, 35.0, 12.0, 8.0, 9.0, FALSE, NOW(), 'u-client-002');

-- ============================================================
-- MEAL FOODS
-- ============================================================
INSERT INTO meal_foods (id, meal_id, food_id, servings, total_calories, total_protein, total_carbs, total_fats,
                        total_fiber, total_sugar, created_date, created_by)
VALUES ('mf-001', 'meal-001', 'food-006', 1.0, 389, 17.0, 66.0, 7.0, 10.0, 1.1, NOW(), 'u-client-001'),
       ('mf-002', 'meal-001', 'food-007', 1.0, 120, 24.0, 3.0, 2.5, 0.5, 2.0, NOW(), 'u-client-001'),
       ('mf-003', 'meal-001', 'food-011', 1.0, 89, 1.1, 23.0, 0.3, 2.6, 12.0, NOW(), 'u-client-001'),
       ('mf-004', 'meal-002', 'food-001', 2.0, 330, 62.0, 0.0, 7.2, 0.0, 0.0, NOW(), 'u-client-001'),
       ('mf-005', 'meal-002', 'food-002', 2.0, 222, 5.2, 46.0, 1.8, 3.6, 0.8, NOW(), 'u-client-001'),
       ('mf-006', 'meal-004', 'food-009', 2.0, 416, 40.0, 0.0, 26.0, 0.0, 0.0, NOW(), 'u-client-001'),
       ('mf-007', 'meal-004', 'food-008', 1.5, 129, 2.4, 30.0, 0.15, 4.5, 6.3, NOW(), 'u-client-001');

-- ============================================================
-- WATER INTAKE
-- ============================================================
INSERT INTO water_intake (id, client_id, intake_date, amount_ml, target_ml, intake_time, created_date, created_by)
VALUES
-- cp-001 today (3100ml target)
('wi-001', 'cp-001', CURRENT_DATE, 350, 3100, NOW()::DATE + TIME '07:00', NOW(), 'u-client-001'),
('wi-002', 'cp-001', CURRENT_DATE, 500, 3100, NOW()::DATE + TIME '09:30', NOW(), 'u-client-001'),
('wi-003', 'cp-001', CURRENT_DATE, 400, 3100, NOW()::DATE + TIME '12:00', NOW(), 'u-client-001'),
('wi-004', 'cp-001', CURRENT_DATE, 600, 3100, NOW()::DATE + TIME '15:00', NOW(), 'u-client-001'),
-- cp-001 yesterday
('wi-005', 'cp-001', CURRENT_DATE - 1, 350, 3100, NOW()::DATE - INTERVAL '1 day' + TIME '07:15',
 NOW() - INTERVAL '1 day', 'u-client-001'),
('wi-006', 'cp-001', CURRENT_DATE - 1, 500, 3100, NOW()::DATE - INTERVAL '1 day' + TIME '11:00',
 NOW() - INTERVAL '1 day', 'u-client-001'),
('wi-007', 'cp-001', CURRENT_DATE - 1, 500, 3100, NOW()::DATE - INTERVAL '1 day' + TIME '14:30',
 NOW() - INTERVAL '1 day', 'u-client-001'),
('wi-008', 'cp-001', CURRENT_DATE - 1, 700, 3100, NOW()::DATE - INTERVAL '1 day' + TIME '18:00',
 NOW() - INTERVAL '1 day', 'u-client-001'),
('wi-009', 'cp-001', CURRENT_DATE - 1, 400, 3100, NOW()::DATE - INTERVAL '1 day' + TIME '21:00',
 NOW() - INTERVAL '1 day', 'u-client-001'),
-- cp-002 today
('wi-010', 'cp-002', CURRENT_DATE, 250, 2030, NOW()::DATE + TIME '08:00', NOW(), 'u-client-002'),
('wi-011', 'cp-002', CURRENT_DATE, 500, 2030, NOW()::DATE + TIME '12:30', NOW(), 'u-client-002'),
-- cp-003 today
('wi-012', 'cp-003', CURRENT_DATE, 400, 3325, NOW()::DATE + TIME '07:30', NOW(), 'u-client-003'),
('wi-013', 'cp-003', CURRENT_DATE, 600, 3325, NOW()::DATE + TIME '10:00', NOW(), 'u-client-003'),
('wi-014', 'cp-003', CURRENT_DATE, 500, 3325, NOW()::DATE + TIME '13:30', NOW(), 'u-client-003');

-- ============================================================
-- EMAIL FAILURE LOG (sample failed emails)
-- ============================================================
INSERT INTO email_failure_log (id, recipient_email, email_type, email_content, failure_reason, error_message,
                               attempt_count, retry_scheduled, next_retry_at, last_attempt_at, created_date, created_by)
VALUES ('efl-001', 'ihor.lysenko@gmail.com', 'VERIFICATION', NULL, 'Send failed', 'Connection timeout to mail server',
        2, TRUE, NOW() + INTERVAL '15 minutes', NOW() - INTERVAL '10 minutes', NOW() - INTERVAL '30 minutes', 'SYSTEM'),
       ('efl-002', 'tetiana.bondarenko@gmail.com', 'VERIFICATION', NULL, 'Authentication failed', 'SMTP auth error 535',
        1, TRUE, NOW() + INTERVAL '5 minutes', NOW() - INTERVAL '5 minutes', NOW() - INTERVAL '10 minutes', 'SYSTEM'),
       ('efl-003', 'old.user@example.com', 'PASSWORD_RESET', NULL, 'Send failed', 'Invalid recipient address', 3, FALSE,
        NULL, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '3 hours', 'SYSTEM');