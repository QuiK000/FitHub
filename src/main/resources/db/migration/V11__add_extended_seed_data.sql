-- ============================================================
-- V11: Extended seed data — UUID-based IDs only
-- All INSERT/UPDATE referencing legacy non-UUID IDs removed.
-- ============================================================

-- ============================================================
-- NEW USERS
-- ============================================================
INSERT INTO users (id, user_email, user_password, user_enabled, created_date, created_by, last_modified_date,
                   last_modified_by)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'oleksandr.hrytsenko@gmail.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
        NOW() - INTERVAL '25 days', 'SYSTEM', NOW() - INTERVAL '25 days', 'SYSTEM'),
       ('550e8400-e29b-41d4-a716-446655440002', 'mariia.kuchma@gmail.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
        NOW() - INTERVAL '18 days', 'SYSTEM', NOW() - INTERVAL '18 days', 'SYSTEM'),
       ('550e8400-e29b-41d4-a716-446655440003', 'serhii.pylypenko@gmail.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
        NOW() - INTERVAL '12 days', 'SYSTEM', NOW() - INTERVAL '12 days', 'SYSTEM'),
       ('550e8400-e29b-41d4-a716-446655440004', 'daryna.tkachuk@gmail.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', FALSE,
        NOW() - INTERVAL '3 days', 'SYSTEM', NULL, NULL),
       ('550e8400-e29b-41d4-a716-446655440005', 'kateryna.vovk@fithub.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', TRUE,
        NOW() - INTERVAL '20 days', 'SYSTEM', NOW() - INTERVAL '20 days', 'SYSTEM');

-- ============================================================
-- USER ROLES
-- ============================================================
INSERT INTO user_roles (user_id, role_id)
VALUES ('550e8400-e29b-41d4-a716-446655440001', '00000000-0000-0000-0000-000000000003'),
       ('550e8400-e29b-41d4-a716-446655440002', '00000000-0000-0000-0000-000000000003'),
       ('550e8400-e29b-41d4-a716-446655440003', '00000000-0000-0000-0000-000000000003'),
       ('550e8400-e29b-41d4-a716-446655440004', '00000000-0000-0000-0000-000000000003'),
       ('550e8400-e29b-41d4-a716-446655440005', '00000000-0000-0000-0000-000000000002');

-- ============================================================
-- NEW SPECIALIZATION
-- ============================================================
INSERT INTO specializations (id, name, description, active)
VALUES ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', 'Functional Training',
        'Movement-based training focusing on everyday functional patterns, mobility and stability.', TRUE);

-- ============================================================
-- NEW TRAINER PROFILE (Катерина Вовк)
-- ============================================================
INSERT INTO trainer_profiles (id, first_name, last_name, experience_years, description, active, user_id,
                              created_date, created_by, last_modified_date, last_modified_by)
VALUES ('6ba7b811-9dad-11d1-80b4-00c04fd430c8', 'Катерина', 'Вовк', 4,
        'Functional movement specialist and kettlebell instructor. Works with beginners and intermediate athletes.',
        TRUE, '550e8400-e29b-41d4-a716-446655440005',
        NOW() - INTERVAL '19 days', '550e8400-e29b-41d4-a716-446655440005',
        NOW() - INTERVAL '15 days', '550e8400-e29b-41d4-a716-446655440005');

INSERT INTO trainer_specialization (trainer_id, specialization_id)
VALUES ('6ba7b811-9dad-11d1-80b4-00c04fd430c8', '6ba7b810-9dad-11d1-80b4-00c04fd430c8');

-- ============================================================
-- NEW CLIENT PROFILES
-- ============================================================
INSERT INTO client_profiles (id, first_name, last_name, phone, birth_date, height, weight, daily_water_target,
                             client_gender, active, user_id, created_date, created_by, last_modified_date,
                             last_modified_by)
VALUES ('6ba7b812-9dad-11d1-80b4-00c04fd430c8', 'Олександр', 'Гриценко', '+380971234509', '1991-07-14', 179.0, 78.0,
        2730, 'MALE', TRUE, '550e8400-e29b-41d4-a716-446655440001',
        NOW() - INTERVAL '24 days', '550e8400-e29b-41d4-a716-446655440001',
        NOW() - INTERVAL '24 days', '550e8400-e29b-41d4-a716-446655440001'),
       ('6ba7b813-9dad-11d1-80b4-00c04fd430c8', 'Марія', 'Кучма', '+380971234510', '1999-03-22', 163.0, 57.0, 1995,
        'FEMALE', TRUE, '550e8400-e29b-41d4-a716-446655440002',
        NOW() - INTERVAL '17 days', '550e8400-e29b-41d4-a716-446655440002',
        NOW() - INTERVAL '5 days', '550e8400-e29b-41d4-a716-446655440002'),
       ('6ba7b814-9dad-11d1-80b4-00c04fd430c8', 'Сергій', 'Пилипенко', '+380971234511', '1995-11-08', 176.0, 90.0,
        3150, 'MALE', TRUE, '550e8400-e29b-41d4-a716-446655440003',
        NOW() - INTERVAL '11 days', '550e8400-e29b-41d4-a716-446655440003',
        NOW() - INTERVAL '11 days', '550e8400-e29b-41d4-a716-446655440003');

-- ============================================================
-- NEW MEMBERSHIPS
-- ============================================================
INSERT INTO memberships (id, membership_type, membership_status, start_date, end_date, visits_left, freeze_date,
                         duration_months, client_id, created_date, created_by, last_modified_date, last_modified_by)
VALUES ('6ba7b815-9dad-11d1-80b4-00c04fd430c8', 'MONTHLY', 'ACTIVE',
        NOW() - INTERVAL '22 days', NOW() + INTERVAL '8 days', NULL, NULL, 1,
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '23 days', 'SYSTEM', NOW() - INTERVAL '22 days', 'SYSTEM'),

       ('6ba7b816-9dad-11d1-80b4-00c04fd430c8', 'VISITS', 'ACTIVE',
        NOW() - INTERVAL '15 days', NULL, 10, NULL, NULL,
        '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '16 days', 'SYSTEM', NOW() - INTERVAL '15 days', 'SYSTEM'),

       ('6ba7b817-9dad-11d1-80b4-00c04fd430c8', 'MONTHLY', 'CREATED',
        NULL, NULL, NULL, NULL, 1,
        '6ba7b814-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '10 days', 'SYSTEM', NULL, NULL);

-- ============================================================
-- PAYMENTS
-- ============================================================
INSERT INTO payments (id, amount, currency, payment_status, payment_date, client_id, membership_id, created_date,
                      created_by, last_modified_date, last_modified_by)
VALUES ('6ba7b820-9dad-11d1-80b4-00c04fd430c8', 800.00, 'UAH', 'PAID',
        NOW() - INTERVAL '22 days',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', '6ba7b815-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '22 days', 'SYSTEM', NOW() - INTERVAL '22 days', 'SYSTEM'),

       ('6ba7b821-9dad-11d1-80b4-00c04fd430c8', 600.00, 'UAH', 'PAID',
        NOW() - INTERVAL '15 days',
        '6ba7b813-9dad-11d1-80b4-00c04fd430c8', '6ba7b816-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '15 days', 'SYSTEM', NOW() - INTERVAL '15 days', 'SYSTEM');

-- ============================================================
-- NEW TRAINING SESSIONS (Kateryna — UUID trainer)
-- ============================================================
INSERT INTO training_sessions (id, training_type, start_time, end_time, max_participants, training_status, trainer_id,
                               created_date, created_by, last_modified_date, last_modified_by)
VALUES ('6ba7b822-9dad-11d1-80b4-00c04fd430c8', 'GROUP',
        NOW() + INTERVAL '2 days 10:00', NOW() + INTERVAL '2 days 11:00', 8, 'SCHEDULED',
        '6ba7b811-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '5 days', '550e8400-e29b-41d4-a716-446655440005', NULL, NULL),

       ('6ba7b823-9dad-11d1-80b4-00c04fd430c8', 'GROUP',
        NOW() + INTERVAL '4 days 18:00', NOW() + INTERVAL '4 days 19:00', 8, 'SCHEDULED',
        '6ba7b811-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '4 days', '550e8400-e29b-41d4-a716-446655440005', NULL, NULL),

       ('6ba7b826-9dad-11d1-80b4-00c04fd430c8', 'GROUP',
        NOW() - INTERVAL '4 days 10:00', NOW() - INTERVAL '4 days 11:00', 8, 'COMPLETED',
        '6ba7b811-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '10 days', '550e8400-e29b-41d4-a716-446655440005',
        NOW() - INTERVAL '4 days', '550e8400-e29b-41d4-a716-446655440005');

-- Junction: UUID clients → UUID sessions only
INSERT INTO training_client (training_id, client_id)
VALUES ('6ba7b822-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8'),
       ('6ba7b822-9dad-11d1-80b4-00c04fd430c8', '6ba7b813-9dad-11d1-80b4-00c04fd430c8'),
       ('6ba7b823-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8'),

       ('6ba7b826-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8');

-- ============================================================
-- ATTENDANCES
-- ============================================================
INSERT INTO attendances (id, check_in_time, client_id, session_id, created_date, created_by, last_modified_date,
                         last_modified_by)
VALUES ('6ba7b827-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '4 days 10:03',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b826-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '4 days', '550e8400-e29b-41d4-a716-446655440005', NULL, NULL);

-- ============================================================
-- WORKOUT PLAN ASSIGNMENTS
-- ============================================================
INSERT INTO client_workout_plans (id, client_id, workout_plan_id, assigned_date, start_date, end_date, status,
                                  completion_percentage, created_date, created_by, last_modified_date, last_modified_by)
VALUES ('6ba7b830-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', 'wp-001',
        NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days', NULL, 'IN_PROGRESS', 22.0,
        NOW() - INTERVAL '20 days', 'u-trainer-01',
        NOW() - INTERVAL '5 days', 'u-trainer-01'),

       ('6ba7b831-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b813-9dad-11d1-80b4-00c04fd430c8', 'wp-004',
        NOW() - INTERVAL '14 days', NOW() - INTERVAL '12 days', NULL, 'IN_PROGRESS', 10.0,
        NOW() - INTERVAL '14 days', 'u-trainer-02',
        NOW() - INTERVAL '2 days', 'u-trainer-02'),

       ('6ba7b832-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b814-9dad-11d1-80b4-00c04fd430c8', 'wp-007',
        NOW() - INTERVAL '9 days', NULL, NULL, 'ASSIGNED', 0.0,
        NOW() - INTERVAL '9 days', 'u-trainer-03', NULL, NULL);

-- ============================================================
-- WORKOUT LOGS
-- ============================================================
INSERT INTO workout_logs (id, client_workout_plan_id, exercise_id, workout_date, sets_completed, reps_completed,
                          weight_used, duration_seconds, notes, difficulty_rating, created_date, created_by,
                          last_modified_date, last_modified_by)
VALUES ('6ba7b840-9dad-11d1-80b4-00c04fd430c8', '6ba7b830-9dad-11d1-80b4-00c04fd430c8', 'ex-001',
        NOW() - INTERVAL '17 days', 3, 5, 60.0, NULL, 'First time squatting with bar, coach watched form', 8,
        NOW() - INTERVAL '17 days', '550e8400-e29b-41d4-a716-446655440001', NULL, NULL),

       ('6ba7b841-9dad-11d1-80b4-00c04fd430c8', '6ba7b830-9dad-11d1-80b4-00c04fd430c8', 'ex-003',
        NOW() - INTERVAL '17 days', 3, 5, 50.0, NULL, 'Bar path felt off on last set', 7,
        NOW() - INTERVAL '17 days', '550e8400-e29b-41d4-a716-446655440001', NULL, NULL),

       ('6ba7b842-9dad-11d1-80b4-00c04fd430c8', '6ba7b830-9dad-11d1-80b4-00c04fd430c8', 'ex-002',
        NOW() - INTERVAL '14 days', 1, 5, 80.0, NULL, 'Heavier than expected, kept form tight', 9,
        NOW() - INTERVAL '14 days', '550e8400-e29b-41d4-a716-446655440001', NULL, NULL),

       ('6ba7b843-9dad-11d1-80b4-00c04fd430c8', '6ba7b830-9dad-11d1-80b4-00c04fd430c8', 'ex-001',
        NOW() - INTERVAL '10 days', 3, 5, 62.5, NULL, 'Added 2.5kg, depth improved', 7,
        NOW() - INTERVAL '10 days', '550e8400-e29b-41d4-a716-446655440001', NULL, NULL),

       ('6ba7b847-9dad-11d1-80b4-00c04fd430c8', '6ba7b831-9dad-11d1-80b4-00c04fd430c8', 'ex-011',
        NOW() - INTERVAL '11 days', 1, NULL, NULL, 1500, 'Easy 25min jog, catching breath', 5,
        NOW() - INTERVAL '11 days', '550e8400-e29b-41d4-a716-446655440002', NULL, NULL),

       ('6ba7b848-9dad-11d1-80b4-00c04fd430c8', '6ba7b831-9dad-11d1-80b4-00c04fd430c8', 'ex-014',
        NOW() - INTERVAL '9 days', 3, NULL, NULL, 90, '3x30s plank, core was burning', 7,
        NOW() - INTERVAL '9 days', '550e8400-e29b-41d4-a716-446655440002', NULL, NULL),

       ('6ba7b849-9dad-11d1-80b4-00c04fd430c8', '6ba7b831-9dad-11d1-80b4-00c04fd430c8', 'ex-011',
        NOW() - INTERVAL '6 days', 1, NULL, NULL, 1800, 'Full 30min run! Massive improvement', 6,
        NOW() - INTERVAL '6 days', '550e8400-e29b-41d4-a716-446655440002', NULL, NULL);

-- ============================================================
-- BODY MEASUREMENTS
-- ============================================================
INSERT INTO body_measurements (id, client_id, measurement_date, weight, body_fat_percentage, muscle_mass, bmi, bmr,
                               body_water_percentage, body_mass, visceral_fat_level, notes, created_date, created_by,
                               last_modified_date, last_modified_by)
VALUES ('6ba7b851-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '23 days', 79.5, 21.0, 58.5, 24.8, 1920, 60.0, 3.4, 4,
        'Initial assessment. Good starting base.',
        NOW() - INTERVAL '23 days', '550e8400-e29b-41d4-a716-446655440005', NULL, NULL),

       ('6ba7b852-9dad-11d1-80b4-00c04fd430c8', '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '16 days', 58.0, 27.5, 39.5, 21.8, 1398, 57.5, 2.3, 3,
        'Initial measurement. Goal: reduce fat, increase endurance.',
        NOW() - INTERVAL '16 days', '550e8400-e29b-41d4-a716-446655440005', NULL, NULL);

-- ============================================================
-- GOALS
-- ============================================================
INSERT INTO goals (id, client_id, title, description, goal_type, start_value, target_value, current_value, unit,
                   start_date, target_date, completion_date, status, progress_percentage, notes, version, created_date,
                   created_by)
VALUES ('6ba7b860-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b813-9dad-11d1-80b4-00c04fd430c8', 'Run 10km',
        'First running milestone — build from scratch to 10km', 'CARDIO',
        0.0, 10.0, 1.5, 'KM',
        NOW() - INTERVAL '11 days', NOW() + INTERVAL '50 days',
        NULL, 'ACTIVE', 15.0,
        'Started jogging program with trainer.', 0,
        NOW() - INTERVAL '11 days', '550e8400-e29b-41d4-a716-446655440002'),

       ('6ba7b861-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', 'Reach 81kg body weight',
        'Lean bulk — add 3kg of quality muscle mass', 'WEIGHT',
        78.0, 81.0, 79.5, 'KG',
        NOW() - INTERVAL '22 days', NOW() + INTERVAL '68 days',
        NULL, 'ACTIVE', 50.0,
        'Gained 1.5kg in first 3 weeks, mostly lean.', 0,
        NOW() - INTERVAL '22 days', '550e8400-e29b-41d4-a716-446655440001');

-- ============================================================
-- PERSONAL RECORDS
-- ============================================================
INSERT INTO personal_records (id, client_id, exercise_id, record_type, value, unit, record_date, previous_record,
                              notes, is_current_best, created_date, created_by)
VALUES ('6ba7b872-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', 'ex-001', 'ONE_REP_MAX',
        70.0, 'KG', NOW() - INTERVAL '5 days', NULL,
        'First tested max squat. Trainer confirmed depth.',
        TRUE, NOW() - INTERVAL '5 days', '550e8400-e29b-41d4-a716-446655440001');

-- ============================================================
-- WATER INTAKE
-- ============================================================
INSERT INTO water_intake (id, client_id, intake_date, amount_ml, target_ml, intake_time, created_date, created_by)
VALUES ('6ba7b885-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        CURRENT_DATE, 400, 2730, CURRENT_DATE::TIMESTAMP + TIME '08:00',
        NOW(), '550e8400-e29b-41d4-a716-446655440001'),
       ('6ba7b886-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        CURRENT_DATE, 500, 2730, CURRENT_DATE::TIMESTAMP + TIME '12:30',
        NOW(), '550e8400-e29b-41d4-a716-446655440001'),
       ('6ba7b887-9dad-11d1-80b4-00c04fd430c8', '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        CURRENT_DATE - 1, 2500, 2730,
        (CURRENT_DATE - INTERVAL '1 day')::TIMESTAMP + TIME '21:00',
        NOW() - INTERVAL '1 day', '550e8400-e29b-41d4-a716-446655440001'),
       ('6ba7b888-9dad-11d1-80b4-00c04fd430c8', '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        CURRENT_DATE, 250, 1995, CURRENT_DATE::TIMESTAMP + TIME '08:30',
        NOW(), '550e8400-e29b-41d4-a716-446655440002'),
       ('6ba7b889-9dad-11d1-80b4-00c04fd430c8', '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        CURRENT_DATE, 350, 1995, CURRENT_DATE::TIMESTAMP + TIME '13:00',
        NOW(), '550e8400-e29b-41d4-a716-446655440002');

-- ============================================================
-- MEAL PLANS
-- ============================================================
INSERT INTO meal_plans (id, client_id, plan_date, total_calories, target_calories, protein, carbs, fats, fiber, sugar,
                        target_protein, target_carbs, target_fats, target_fiber, target_sugar, notes, created_date,
                        created_by)
VALUES ('6ba7b890-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', CURRENT_DATE,
        2280, 2300, 175.0, 240.0, 70.0, 25.0, 28.0,
        180.0, 245.0, 72.0, 28.0, 30.0,
        'Lean bulk day. Trainer-approved macros.', NOW(), '550e8400-e29b-41d4-a716-446655440001'),

       ('6ba7b891-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b813-9dad-11d1-80b4-00c04fd430c8', CURRENT_DATE,
        1540, 1550, 118.0, 168.0, 42.0, 20.0, 25.0,
        120.0, 170.0, 43.0, 22.0, 27.0,
        'Calorie deficit. Rest day — lower carbs.', NOW(), '550e8400-e29b-41d4-a716-446655440002');

-- ============================================================
-- PROGRESS PHOTOS
-- ============================================================
INSERT INTO progress_photos (id, client_id, photo_date, photo_url, angle, notes, measurement_id, created_date,
                             created_by)
VALUES ('6ba7b8a2-9dad-11d1-80b4-00c04fd430c8',
        '6ba7b812-9dad-11d1-80b4-00c04fd430c8', NOW() - INTERVAL '22 days',
        'https://storage.fithub.com/progress/cp009/front_day0.jpg', 'FRONT',
        'Starting photo. Reference point.',
        '6ba7b851-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '22 days', '550e8400-e29b-41d4-a716-446655440001');

-- ============================================================
-- VERIFICATION TOKEN
-- ============================================================
INSERT INTO verification_tokens (id, token, token_type, user_id, expires_at, used)
VALUES ('6ba7b8b0-9dad-11d1-80b4-00c04fd430c8',
        'a3f2c1d4-e5b6-47f8-9012-abcdef123456',
        'EMAIL_VERIFICATION',
        '550e8400-e29b-41d4-a716-446655440004',
        NOW() + INTERVAL '24 hours',
        FALSE);

-- ============================================================
-- EMAIL FAILURE LOG
-- ============================================================
INSERT INTO email_failure_log (id, recipient_email, email_type, failure_reason, error_message,
                               attempt_count, retry_scheduled, next_retry_at, last_attempt_at, created_date, created_by)
VALUES ('6ba7b8c0-9dad-11d1-80b4-00c04fd430c8',
        'daryna.tkachuk@gmail.com', 'VERIFICATION',
        'Send failed', 'Connection reset by peer',
        1, TRUE,
        NOW() + INTERVAL '5 minutes',
        NOW() - INTERVAL '2 minutes',
        NOW() - INTERVAL '3 days', 'SYSTEM');