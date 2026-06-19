-- ============================================================
-- V29: Notifications, reviews, session waitlist
-- ============================================================

-- ============================================================
-- NOTIFICATIONS - Spread across the year
-- ============================================================
INSERT INTO notifications (id, recipient_id, notification_type, priority, title, message, is_read, read_at, action_url, created_date, created_by)
VALUES
-- Ivan's notifications
('nf000000-0000-0000-0000-000000000001', 'u0000000-0000-0000-0000-000000000001', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Welcome back!', 'Your yearly membership has been activated. Let''s crush those goals!',
 TRUE, NOW() - INTERVAL '60 days', '/memberships', NOW() - INTERVAL '65 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000002', 'u0000000-0000-0000-0000-000000000001', 'GOAL_ACHIEVED', 'HIGH',
 'Goal Achieved!', 'Congratulations! You reached your weight loss goal of 79kg!',
 TRUE, NOW() - INTERVAL '30 days', '/progress', NOW() - INTERVAL '30 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000003', 'u0000000-0000-0000-0000-000000000001', 'NEW_WORKOUT_PLAN', 'NORMAL',
 'New Workout Plan', 'Trainer Taras has assigned you the Muscle Mass Builder program.',
 TRUE, NOW() - INTERVAL '130 days', '/workouts', NOW() - INTERVAL '130 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000004', 'u0000000-0000-0000-0000-000000000001', 'SESSION_REMINDER', 'NORMAL',
 'Session Tomorrow', 'You have a Strength Training session tomorrow at 9:00 AM.',
 TRUE, NOW() - INTERVAL '11 days', '/sessions', NOW() - INTERVAL '11 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000005', 'u0000000-0000-0000-0000-000000000001', 'MILESTONE_REACHED', 'HIGH',
 '100 Workouts!', 'Amazing! You''ve completed 100 logged workouts. Keep it up!',
 FALSE, NULL, '/progress', NOW() - INTERVAL '5 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000006', 'u0000000-0000-0000-0000-000000000001', 'SESSION_REMINDER', 'NORMAL',
 'Upcoming Session', 'Don''t forget your group training session tomorrow!',
 FALSE, NULL, '/sessions', NOW() - INTERVAL '1 day', 'SYSTEM'),

-- Oksana K.'s notifications
('nf000000-0000-0000-0000-000000000007', 'u0000000-0000-0000-0000-000000000002', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Monthly Membership', 'Your monthly membership has been renewed. Enjoy your training!',
 TRUE, NOW() - INTERVAL '20 days', '/memberships', NOW() - INTERVAL '20 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000008', 'u0000000-0000-0000-0000-000000000002', 'GOAL_ACHIEVED', 'HIGH',
 'Weight Goal Reached!', 'You''ve reached your target weight of 57kg. Amazing transformation!',
 TRUE, NOW() - INTERVAL '60 days', '/progress', NOW() - INTERVAL '60 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000009', 'u0000000-0000-0000-0000-000000000002', 'WORKOUT_COMPLETED', 'NORMAL',
 'Workout Logged', 'Great job completing your HIIT session today!',
 TRUE, NOW() - INTERVAL '3 days', '/workouts', NOW() - INTERVAL '3 days', 'SYSTEM'),

-- Dmytro M.'s notifications
('nf000000-0000-0000-0000-000000000010', 'u0000000-0000-0000-0000-000000000003', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Yearly Membership', 'Your yearly membership is active. Full access unlocked!',
 TRUE, NOW() - INTERVAL '35 days', '/memberships', NOW() - INTERVAL '35 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000011', 'u0000000-0000-0000-0000-000000000003', 'GOAL_ACHIEVED', 'HIGH',
 'Weight Loss Goal!', 'You''ve lost 7kg and reached your target weight. Incredible work!',
 TRUE, NOW() - INTERVAL '30 days', '/progress', NOW() - INTERVAL '30 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000012', 'u0000000-0000-0000-0000-000000000003', 'NEW_WORKOUT_PLAN', 'NORMAL',
 'Recomposition Plan', 'Trainer Maksym has assigned you the Body Recomposition program.',
 TRUE, NOW() - INTERVAL '200 days', '/workouts', NOW() - INTERVAL '200 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000013', 'u0000000-0000-0000-0000-000000000003', 'PAYMENT_SUCCESS', 'NORMAL',
 'Payment Confirmed', 'Your payment of 15,000 UAH for yearly membership has been processed.',
 TRUE, NOW() - INTERVAL '35 days', '/memberships', NOW() - INTERVAL '35 days', 'SYSTEM'),

-- Hanna's notifications
('nf000000-0000-0000-0000-000000000014', 'u0000000-0000-0000-0000-000000000006', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Visit Pack Activated', 'Your 12-visit pack is now active. Enjoy your sessions!',
 TRUE, NOW() - INTERVAL '10 days', '/memberships', NOW() - INTERVAL '10 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000015', 'u0000000-0000-0000-0000-000000000006', 'SESSION_REMINDER', 'NORMAL',
 'Session Tomorrow', 'You have a Strength Training session at 9:00 AM tomorrow.',
 TRUE, NOW() - INTERVAL '2 days', '/sessions', NOW() - INTERVAL '2 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000016', 'u0000000-0000-0000-0000-000000000006', 'MEMBERSHIP_EXPIRING', 'URGENT',
 'Visits Running Low', 'You have only 2 visits remaining in your pack. Consider renewing.',
 FALSE, NULL, '/memberships', NOW() - INTERVAL '5 days', 'SYSTEM'),

-- Roman's notifications
('nf000000-0000-0000-0000-000000000017', 'u0000000-0000-0000-0000-000000000011', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Welcome!', 'Your yearly membership is now active. Start your muscle gain journey!',
 TRUE, NOW() - INTERVAL '50 days', '/memberships', NOW() - INTERVAL '50 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000018', 'u0000000-0000-0000-0000-000000000011', 'NEW_WORKOUT_PLAN', 'NORMAL',
 'Muscle Mass Program', 'Trainer Taras has assigned you the Muscle Mass Builder program.',
 TRUE, NOW() - INTERVAL '40 days', '/workouts', NOW() - INTERVAL '40 days', 'SYSTEM'),

-- Bohdan's notifications (new member)
('nf000000-0000-0000-0000-000000000019', 'u0000000-0000-0000-0000-000000000015', 'MEMBERSHIP_ACTIVATED', 'HIGH',
 'Welcome to FitHub!', 'Your monthly membership is active. Welcome to the community!',
 FALSE, NULL, '/memberships', NOW() - INTERVAL '8 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000020', 'u0000000-0000-0000-0000-000000000015', 'NEW_WORKOUT_PLAN', 'NORMAL',
 'Your First Program', 'Trainer Andrii has assigned you the Beginner Basics program.',
 FALSE, NULL, '/workouts', NOW() - INTERVAL '5 days', 'SYSTEM'),

-- General announcements
('nf000000-0000-0000-0000-000000000021', 'u0000000-0000-0000-0000-000000000001', 'GENERAL_ANNOUNCEMENT', 'LOW',
 'Holiday Schedule', 'FitHub will have modified hours during the holiday season. Check the schedule.',
 TRUE, NOW() - INTERVAL '100 days', NULL, NOW() - INTERVAL '100 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000022', 'u0000000-0000-0000-0000-000000000002', 'GENERAL_ANNOUNCEMENT', 'LOW',
 'New Classes Available', 'We''ve added new yoga and pilates classes to the schedule!',
 TRUE, NOW() - INTERVAL '80 days', NULL, NOW() - INTERVAL '80 days', 'SYSTEM'),
('nf000000-0000-0000-0000-000000000023', 'u0000000-0000-0000-0000-000000000003', 'GENERAL_ANNOUNCEMENT', 'LOW',
 'Nutrition Workshop', 'Join our free nutrition workshop this Saturday at 11 AM!',
 TRUE, NOW() - INTERVAL '50 days', NULL, NOW() - INTERVAL '50 days', 'SYSTEM');

-- ============================================================
-- TRAINER REVIEWS - Spread over the year
-- ============================================================
INSERT INTO trainer_reviews (id, client_id, trainer_id, rating, comment, professionalism_rating, knowledge_rating, communication_rating, motivation_rating, visible, is_visible, created_date, created_by)
VALUES
('rv100000-0000-0000-0000-000000000001', 'q0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 5,
 'Oleksiy is an exceptional trainer. His strength programs are well-structured and he always pushes you to do your best. Highly recommend!',
 5, 5, 5, 5, TRUE, TRUE, NOW() - INTERVAL '200 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000002', 'q0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000002', 5,
 'Iryna''s yoga classes are amazing. She creates a peaceful atmosphere while still challenging you physically.',
 5, 5, 5, 4, TRUE, TRUE, NOW() - INTERVAL '180 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000003', 'q0000000-0000-0000-0000-000000000003', 'p0000000-0000-0000-0000-000000000005', 4,
 'Great trainer for body recomposition. Knows nutrition well and designs effective programs. Sometimes sessions run a bit over schedule.',
 5, 5, 4, 4, TRUE, TRUE, NOW() - INTERVAL '150 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000004', 'q0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000001', 5,
 'Perfect for beginners! Oleksiy explains everything clearly and makes sure your form is correct before adding weight.',
 5, 5, 5, 5, TRUE, TRUE, NOW() - INTERVAL '100 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000005', 'q0000000-0000-0000-0000-000000000005', 'p0000000-0000-0000-0000-000000000004', 4,
 'Good cardio sessions. Yuriia keeps the energy high and makes endurance training fun. Would like more variety in exercises.',
 4, 4, 5, 4, TRUE, TRUE, NOW() - INTERVAL '80 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000006', 'q0000000-0000-0000-0000-000000000009', 'p0000000-0000-0000-0000-000000000003', 5,
 'Andrii''s functional training is exactly what I needed. Improved my mobility and core strength significantly.',
 5, 5, 5, 5, TRUE, TRUE, NOW() - INTERVAL '60 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000007', 'q0000000-0000-0000-0000-000000000011', 'c0000000-0000-0000-0000-000000000001', 4,
 'Solid strength training program. Oleksiy knows his stuff. The progressive overload approach really works.',
 5, 5, 4, 4, TRUE, TRUE, NOW() - INTERVAL '35 days', 'SYSTEM'),
('rv100000-0000-0000-0000-000000000008', 'q0000000-0000-0000-0000-000000000002', 'p0000000-0000-0000-0000-000000000003', 5,
 'Andrii combines functional training with great motivational energy. Best trainer for overall fitness.',
 5, 5, 5, 5, TRUE, TRUE, NOW() - INTERVAL '170 days', 'SYSTEM');

-- ============================================================
-- SESSION WAITLIST - For popular sessions
-- ============================================================
INSERT INTO session_waitlist (id, session_id, client_id, position, status, joined_at, created_date, created_by)
VALUES
-- Waitlist for popular strength sessions
('sw000000-0000-0000-0000-000000000001', 'ts000000-0000-0000-0000-000000000124', 'q0000000-0000-0000-0000-000000000007', 1, 'WAITING',
 NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 'SYSTEM'),
('sw000000-0000-0000-0000-000000000002', 'ts000000-0000-0000-0000-000000000124', 'q0000000-0000-0000-0000-000000000012', 2, 'WAITING',
 NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 'SYSTEM'),
('sw000000-0000-0000-0000-000000000003', 'ts000000-0000-0000-0000-000000000128', 'q0000000-0000-0000-0000-000000000008', 1, 'WAITING',
 NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 'SYSTEM'),
('sw000000-0000-0000-0000-000000000004', 'ts000000-0000-0000-0000-000000000129', 'q0000000-0000-0000-0000-000000000014', 1, 'WAITING',
 NOW(), NOW(), 'SYSTEM');
