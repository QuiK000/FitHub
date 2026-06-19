-- ============================================================
-- V27: Body measurements, goals, personal records, progress photos
-- ============================================================

-- ============================================================
-- BODY MEASUREMENTS - Monthly for active clients
-- ============================================================
INSERT INTO body_measurements (id, client_id, measurement_date, weight, body_fat_percentage, muscle_mass, bmi, bmr, notes, created_date, created_by)
VALUES
-- Ivan - weight loss journey: 85 -> 81 kg over 10 months
('bm000000-0000-0000-0000-000000000001', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '300 days', 85.0, 22.5, 35.0, 26.3, 1850, 'Starting point', NOW() - INTERVAL '300 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000002', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '270 days', 84.0, 22.0, 35.2, 26.0, 1855, 'Good progress', NOW() - INTERVAL '270 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000003', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '240 days', 83.0, 21.5, 35.5, 25.7, 1860, 'Steady progress', NOW() - INTERVAL '240 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000004', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '210 days', 82.0, 21.0, 35.8, 25.4, 1865, NULL, NOW() - INTERVAL '210 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000005', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '180 days', 81.5, 20.5, 36.0, 25.2, 1870, 'Looking great', NOW() - INTERVAL '180 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000006', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '150 days', 81.0, 20.0, 36.2, 25.1, 1872, NULL, NOW() - INTERVAL '150 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000007', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '120 days', 80.5, 19.8, 36.5, 24.9, 1875, 'Great transformation', NOW() - INTERVAL '120 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000008', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '90 days', 80.0, 19.5, 36.8, 24.8, 1878, NULL, NOW() - INTERVAL '90 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000009', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '60 days', 79.5, 19.2, 37.0, 24.6, 1880, 'Almost at goal', NOW() - INTERVAL '60 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000010', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '30 days', 79.0, 19.0, 37.2, 24.5, 1882, 'Goal reached!', NOW() - INTERVAL '30 days', 'SYSTEM'),

-- Dmytro M. - weight loss: 92 -> 85 kg
('bm000000-0000-0000-0000-000000000011', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '350 days', 92.0, 25.0, 34.0, 29.0, 1920, 'Starting', NOW() - INTERVAL '350 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000012', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '270 days', 89.0, 23.5, 34.5, 28.1, 1910, 'Good progress', NOW() - INTERVAL '270 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000013', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '180 days', 87.0, 22.5, 35.0, 27.5, 1900, 'Consistent', NOW() - INTERVAL '180 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000014', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '90 days', 85.5, 21.5, 35.5, 27.0, 1895, 'Almost there', NOW() - INTERVAL '90 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000015', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '30 days', 85.0, 21.0, 35.8, 26.9, 1890, 'Goal achieved', NOW() - INTERVAL '30 days', 'SYSTEM'),

-- Oksana K. - toning: 60 -> 57 kg
('bm000000-0000-0000-0000-000000000016', 'q0000000-0000-0000-0000-000000000002', NOW() - INTERVAL '300 days', 60.0, 24.0, 22.0, 21.5, 1420, 'Starting', NOW() - INTERVAL '300 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000017', 'q0000000-0000-0000-0000-000000000002', NOW() - INTERVAL '180 days', 58.5, 23.0, 22.5, 21.0, 1425, 'Toning up', NOW() - INTERVAL '180 days', 'SYSTEM'),
('bm000000-0000-0000-0000-000000000018', 'q0000000-0000-0000-0000-000000000002', NOW() - INTERVAL '60 days', 57.0, 22.0, 23.0, 20.4, 1430, 'Great results', NOW() - INTERVAL '60 days', 'SYSTEM');

-- ============================================================
-- GOALS
-- ============================================================
INSERT INTO goals (id, client_id, title, description, goal_type, start_value, target_value, current_value, unit, start_date, target_date, completion_date, status, progress_percentage, notes, created_date, created_by)
VALUES
-- Ivan
('gl000000-0000-0000-0000-000000000001', 'q0000000-0000-0000-0000-000000000001', 'Lose 6kg', 'Reach target weight of 79kg', 'WEIGHT_LOSS', 85.0, 79.0, 79.0, 'KG',
 NOW() - INTERVAL '300 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', 'COMPLETED', 100.0, 'Goal achieved ahead of schedule!',
 NOW() - INTERVAL '300 days', 'SYSTEM'),
('gl000000-0000-0000-0000-000000000002', 'q0000000-0000-0000-0000-000000000001', 'Bench Press 100kg', 'Achieve 1RM of 100kg on bench press', 'STRENGTH', 70.0, 100.0, 95.0, 'KG',
 NOW() - INTERVAL '200 days', NOW() + INTERVAL '60 days', NULL, 'ACTIVE', 83.0, 'Almost there, 5kg to go',
 NOW() - INTERVAL '200 days', 'SYSTEM'),

-- Dmytro M.
('gl000000-0000-0000-0000-000000000003', 'q0000000-0000-0000-0000-000000000003', 'Lose 7kg', 'Reach 85kg for better health', 'WEIGHT_LOSS', 92.0, 85.0, 85.0, 'KG',
 NOW() - INTERVAL '350 days', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', 'COMPLETED', 100.0, 'Excellent transformation!',
 NOW() - INTERVAL '350 days', 'SYSTEM'),
('gl000000-0000-0000-0000-000000000004', 'q0000000-0000-0000-0000-000000000003', 'Run 5km under 25min', 'Improve cardiovascular endurance', 'ENDURANCE', 30.0, 25.0, 27.0, 'MINUTES',
 NOW() - INTERVAL '200 days', NOW() + INTERVAL '30 days', NULL, 'ACTIVE', 40.0, 'Progressing steadily',
 NOW() - INTERVAL '200 days', 'SYSTEM'),

-- Oksana K.
('gl000000-0000-0000-0000-000000000005', 'q0000000-0000-0000-0000-000000000002', 'Tone up and lose 3kg', 'Reach 57kg with better muscle definition', 'WEIGHT_LOSS', 60.0, 57.0, 57.0, 'KG',
 NOW() - INTERVAL '300 days', NOW() - INTERVAL '60 days', NOW() - INTERVAL '60 days', 'COMPLETED', 100.0, 'Perfect results!',
 NOW() - INTERVAL '300 days', 'SYSTEM'),

-- Hanna
('gl000000-0000-0000-0000-000000000006', 'q0000000-0000-0000-0000-000000000006', 'Build strength foundation', 'Complete beginner strength program', 'STRENGTH', 30.0, 50.0, 47.5, 'KG',
 NOW() - INTERVAL '120 days', NOW() + INTERVAL '10 days', NULL, 'ACTIVE', 90.0, 'Almost done with the program',
 NOW() - INTERVAL '120 days', 'SYSTEM'),

-- Roman
('gl000000-0000-0000-0000-000000000007', 'q0000000-0000-0000-0000-000000000011', 'Gain 3kg muscle mass', 'Healthy weight gain with muscle', 'MUSCLE_GAIN', 86.0, 89.0, 87.0, 'KG',
 NOW() - INTERVAL '40 days', NOW() + INTERVAL '80 days', NULL, 'ACTIVE', 33.0, 'On track',
 NOW() - INTERVAL '40 days', 'SYSTEM');

-- ============================================================
-- PERSONAL RECORDS
-- Only latest per client+exercise+record_type (unique constraint)
-- ============================================================
INSERT INTO personal_records (id, client_id, exercise_id, record_type, value, unit, record_date, previous_record, notes, is_current_best, created_date, created_by)
VALUES
-- Ivan: squat 70->95kg
('pr000000-0000-0000-0000-000000000001', 'q0000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'MAX_WEIGHT', 95.0, 'KG', NOW() - INTERVAL '43 days', 90.0, 'Personal best! Progressive overload over 10 months.', TRUE, NOW() - INTERVAL '43 days', 'SYSTEM'),
-- Dmytro: squat 60->80kg
('pr000000-0000-0000-0000-000000000005', 'q0000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001', 'MAX_WEIGHT', 80.0, 'KG', NOW() - INTERVAL '50 days', 75.0, 'Consistent progress over 10 months.', TRUE, NOW() - INTERVAL '50 days', 'SYSTEM'),
-- Hanna: squat 30->47.5kg
('pr000000-0000-0000-0000-000000000008', 'q0000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000001', 'MAX_WEIGHT', 47.5, 'KG', NOW() - INTERVAL '20 days', 40.0, 'New personal best after 4 months of training.', TRUE, NOW() - INTERVAL '20 days', 'SYSTEM'),
-- Oleg: deadlift 80->120kg
('pr000000-0000-0000-0000-000000000011', 'q0000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000002', 'MAX_WEIGHT', 120.0, 'KG', NOW() - INTERVAL '30 days', 110.0, 'Solid deadlift progression.', TRUE, NOW() - INTERVAL '30 days', 'SYSTEM'),
-- Roman: bench 60->82.5kg
('pr000000-0000-0000-0000-000000000012', 'q0000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000003', 'MAX_WEIGHT', 82.5, 'KG', NOW() - INTERVAL '23 days', 80.0, 'Quick progress on bench press.', TRUE, NOW() - INTERVAL '23 days', 'SYSTEM');

-- ============================================================
-- PROGRESS PHOTOS
-- ============================================================
INSERT INTO progress_photos (id, client_id, photo_date, photo_url, angle, notes, measurement_id, created_date, created_by)
VALUES
-- Ivan's monthly progress
('pp000000-0000-0000-0000-000000000001', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '300 days', '/uploads/images/progress/ivan-front-1.jpg', 'FRONT', 'Starting', 'bm000000-0000-0000-0000-000000000001', NOW() - INTERVAL '300 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000002', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '300 days', '/uploads/images/progress/ivan-side-1.jpg', 'SIDE', 'Starting', 'bm000000-0000-0000-0000-000000000001', NOW() - INTERVAL '300 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000003', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '210 days', '/uploads/images/progress/ivan-front-2.jpg', 'FRONT', '3 months in', 'bm000000-0000-0000-0000-000000000004', NOW() - INTERVAL '210 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000004', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '120 days', '/uploads/images/progress/ivan-front-3.jpg', 'FRONT', '6 months, visible abs', 'bm000000-0000-0000-0000-000000000007', NOW() - INTERVAL '120 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000005', 'q0000000-0000-0000-0000-000000000001', NOW() - INTERVAL '30 days', '/uploads/images/progress/ivan-front-4.jpg', 'FRONT', 'Goal reached!', 'bm000000-0000-0000-0000-000000000010', NOW() - INTERVAL '30 days', 'SYSTEM'),

-- Dmytro's progress
('pp000000-0000-0000-0000-000000000006', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '350 days', '/uploads/images/progress/dmytro-front-1.jpg', 'FRONT', 'Starting point', 'bm000000-0000-0000-0000-000000000011', NOW() - INTERVAL '350 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000007', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '180 days', '/uploads/images/progress/dmytro-front-2.jpg', 'FRONT', 'Halfway there', 'bm000000-0000-0000-0000-000000000013', NOW() - INTERVAL '180 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000008', 'q0000000-0000-0000-0000-000000000003', NOW() - INTERVAL '30 days', '/uploads/images/progress/dmytro-front-3.jpg', 'FRONT', 'Goal achieved', 'bm000000-0000-0000-0000-000000000015', NOW() - INTERVAL '30 days', 'SYSTEM'),

-- Oksana's progress
('pp000000-0000-0000-0000-000000000009', 'q0000000-0000-0000-0000-000000000002', NOW() - INTERVAL '300 days', '/uploads/images/progress/oksana-front-1.jpg', 'FRONT', 'Starting', 'bm000000-0000-0000-0000-000000000016', NOW() - INTERVAL '300 days', 'SYSTEM'),
('pp000000-0000-0000-0000-000000000010', 'q0000000-0000-0000-0000-000000000002', NOW() - INTERVAL '60 days', '/uploads/images/progress/oksana-front-2.jpg', 'FRONT', 'Goal reached', 'bm000000-0000-0000-0000-000000000018', NOW() - INTERVAL '60 days', 'SYSTEM');
