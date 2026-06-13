INSERT INTO memberships (id, membership_type, membership_status, start_date, end_date,
                         visits_left, duration_months, client_id, created_date, created_by,
                         last_modified_date, last_modified_by)
VALUES ('aa000000-0000-0000-0000-000000000001',
        'MONTHLY', 'ACTIVE',
        NOW() - INTERVAL '15 days', NOW() + INTERVAL '15 days',
        NULL, 1, 'd0000000-0000-0000-0000-000000000001',
        NOW() - INTERVAL '15 days', 'SYSTEM', NOW() - INTERVAL '15 days', 'SYSTEM'),

       ('aa000000-0000-0000-0000-000000000002',
        'YEARLY', 'ACTIVE',
        NOW() - INTERVAL '60 days', NOW() + INTERVAL '305 days',
        NULL, 12, 'd0000000-0000-0000-0000-000000000002',
        NOW() - INTERVAL '60 days', 'SYSTEM', NOW() - INTERVAL '60 days', 'SYSTEM'),

       ('aa000000-0000-0000-0000-000000000003',
        'VISITS', 'ACTIVE',
        NOW() - INTERVAL '30 days', NOW() + INTERVAL '60 days',
        8, NULL, 'd0000000-0000-0000-0000-000000000003',
        NOW() - INTERVAL '30 days', 'SYSTEM', NOW() - INTERVAL '30 days', 'SYSTEM'),

       ('aa000000-0000-0000-0000-000000000004',
        'MONTHLY', 'EXPIRED',
        NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days',
        NULL, 1, 'd0000000-0000-0000-0000-000000000006',
        NOW() - INTERVAL '60 days', 'SYSTEM', NOW() - INTERVAL '30 days', 'SYSTEM'),

       ('aa000000-0000-0000-0000-000000000005',
        'MONTHLY', 'FROZEN',
        NOW() - INTERVAL '45 days', NOW() - INTERVAL '15 days',
        NULL, 1, 'd0000000-0000-0000-0000-000000000007',
        NOW() - INTERVAL '45 days', 'SYSTEM', NOW() - INTERVAL '15 days', 'SYSTEM');

-- ============================================================
-- ADDITIONAL TRAINING SESSIONS
-- ============================================================
INSERT INTO training_sessions (id, trainer_id, training_type, training_status,
                               start_time, end_time, max_participants,
                               created_date, created_by,
                               last_modified_date, last_modified_by)
VALUES ('bb000000-0000-0000-0000-000000000001',
        'c0000000-0000-0000-0000-000000000001', 'GROUP', 'SCHEDULED',
        NOW() + INTERVAL '6 days', NOW() + INTERVAL '6 days' + INTERVAL '1 hour',
        15, NOW() - INTERVAL '5 days', 'SYSTEM', NOW() - INTERVAL '5 days', 'SYSTEM'),

       ('bb000000-0000-0000-0000-000000000002',
        'c0000000-0000-0000-0000-000000000002', 'PERSONAL', 'SCHEDULED',
        NOW() + INTERVAL '7 days', NOW() + INTERVAL '7 days' + INTERVAL '1.5 hours',
        1, NOW() - INTERVAL '3 days', 'SYSTEM', NOW() - INTERVAL '3 days', 'SYSTEM'),

       ('bb000000-0000-0000-0000-000000000003',
        'c0000000-0000-0000-0000-000000000001', 'GROUP', 'SCHEDULED',
        NOW() + INTERVAL '8 days', NOW() + INTERVAL '8 days' + INTERVAL '1 hour',
        20, NOW() - INTERVAL '4 days', 'SYSTEM', NOW() - INTERVAL '4 days', 'SYSTEM'),

       ('bb000000-0000-0000-0000-000000000006',
        'c0000000-0000-0000-0000-000000000001', 'GROUP', 'COMPLETED',
        NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days' + INTERVAL '1 hour',
        15, NOW() - INTERVAL '15 days', 'SYSTEM', NOW() - INTERVAL '8 days', 'SYSTEM'),

       ('bb000000-0000-0000-0000-000000000007',
        'c0000000-0000-0000-0000-000000000002', 'PERSONAL', 'COMPLETED',
        NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days' + INTERVAL '1 hour',
        1, NOW() - INTERVAL '12 days', 'SYSTEM', NOW() - INTERVAL '6 days', 'SYSTEM'),

       ('bb000000-0000-0000-0000-000000000008',
        'c0000000-0000-0000-0000-000000000001', 'GROUP', 'COMPLETED',
        NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days' + INTERVAL '1 hour',
        15, NOW() - INTERVAL '18 days', 'SYSTEM', NOW() - INTERVAL '10 days', 'SYSTEM');

-- ============================================================
-- ADDITIONAL TRAINING SESSION CLIENTS
-- ============================================================
INSERT INTO training_client (training_id, client_id)
VALUES ('bb000000-0000-0000-0000-000000000001', '6ba7b812-9dad-11d1-80b4-00c04fd430c8'),
       ('bb000000-0000-0000-0000-000000000001', '6ba7b813-9dad-11d1-80b4-00c04fd430c8'),
       ('bb000000-0000-0000-0000-000000000003', 'd0000000-0000-0000-0000-000000000001'),
       ('bb000000-0000-0000-0000-000000000006', '6ba7b812-9dad-11d1-80b4-00c04fd430c8'),
       ('bb000000-0000-0000-0000-000000000006', 'd0000000-0000-0000-0000-000000000003'),
       ('bb000000-0000-0000-0000-000000000008', '6ba7b813-9dad-11d1-80b4-00c04fd430c8'),
       ('bb000000-0000-0000-0000-000000000008', 'd0000000-0000-0000-0000-000000000004');

-- ============================================================
-- ADDITIONAL ATTENDANCES
-- ============================================================
INSERT INTO attendances (id, session_id, client_id, check_in_time,
                         created_date, created_by)
VALUES ('cc000000-0000-0000-0000-000000000001',
        'bb000000-0000-0000-0000-000000000006', '6ba7b812-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', 'SYSTEM'),
       ('cc000000-0000-0000-0000-000000000002',
        'bb000000-0000-0000-0000-000000000006', 'd0000000-0000-0000-0000-000000000003',
        NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days', 'SYSTEM'),
       ('cc000000-0000-0000-0000-000000000003',
        'bb000000-0000-0000-0000-000000000007', '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days', 'SYSTEM'),
       ('cc000000-0000-0000-0000-000000000004',
        'bb000000-0000-0000-0000-000000000008', '6ba7b813-9dad-11d1-80b4-00c04fd430c8',
        NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 'SYSTEM'),
       ('cc000000-0000-0000-0000-000000000005',
        'bb000000-0000-0000-0000-000000000008', 'd0000000-0000-0000-0000-000000000004',
        NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 'SYSTEM');

-- ============================================================
-- ADDITIONAL PAYMENTS
-- ============================================================
INSERT INTO payments (id, membership_id, amount, currency, payment_status, payment_date, client_id,
                      created_date, created_by)
VALUES ('dd000000-0000-0000-0000-000000000001',
        'aa000000-0000-0000-0000-000000000001', 2500.00, 'UAH', 'PAID',
        NOW() - INTERVAL '15 days', 'd0000000-0000-0000-0000-000000000001',
        NOW() - INTERVAL '15 days', 'SYSTEM'),
       ('dd000000-0000-0000-0000-000000000002',
        'aa000000-0000-0000-0000-000000000002', 15000.00, 'UAH', 'PAID',
        NOW() - INTERVAL '60 days', 'd0000000-0000-0000-0000-000000000002',
        NOW() - INTERVAL '60 days', 'SYSTEM'),
       ('dd000000-0000-0000-0000-000000000003',
        'aa000000-0000-0000-0000-000000000004', 2500.00, 'UAH', 'PAID',
        NOW() - INTERVAL '60 days', 'd0000000-0000-0000-0000-000000000006',
        NOW() - INTERVAL '60 days', 'SYSTEM'),
       ('dd000000-0000-0000-0000-000000000004',
        'aa000000-0000-0000-0000-000000000005', 2500.00, 'UAH', 'PAID',
        NOW() - INTERVAL '45 days', 'd0000000-0000-0000-0000-000000000007',
        NOW() - INTERVAL '45 days', 'SYSTEM');

-- ============================================================
-- ADDITIONAL NOTIFICATIONS
-- ============================================================
INSERT INTO notifications (id, recipient_id, notification_type, priority, title, message,
                           is_read, action_url, created_date, created_by)
VALUES ('ee000000-0000-0000-0000-000000000001',
        '550e8400-e29b-41d4-a716-446655440001', 'MEMBERSHIP_ACTIVATED', 'HIGH',
        'Membership Activated', 'Your monthly membership has been activated. Enjoy your training!',
        FALSE, '/memberships', NOW() - INTERVAL '15 days', 'SYSTEM'),

       ('ee000000-0000-0000-0000-000000000002',
        '550e8400-e29b-41d4-a716-446655440001', 'SESSION_REMINDER', 'NORMAL',
        'Upcoming Session', 'You have a group training session tomorrow at 10:00.',
        FALSE, '/sessions', NOW() - INTERVAL '1 day', 'SYSTEM'),

       ('ee000000-0000-0000-0000-000000000003',
        '550e8400-e29b-41d4-a716-446655440002', 'NEW_WORKOUT_PLAN', 'NORMAL',
        'New Workout Plan', 'Trainer Iryna has assigned you a new workout plan!',
        FALSE, '/workouts', NOW() - INTERVAL '3 days', 'SYSTEM'),

       ('ee000000-0000-0000-0000-000000000004',
        '550e8400-e29b-41d4-a716-446655440001', 'GOAL_ACHIEVED', 'HIGH',
        'Goal Achieved!', 'Congratulations! You have reached your weight loss goal.',
        TRUE, '/progress', NOW() - INTERVAL '7 days', 'SYSTEM'),

       ('ee000000-0000-0000-0000-000000000005',
        'a0000000-0000-0000-0000-000000000031', 'PAYMENT_SUCCESS', 'NORMAL',
        'Payment Confirmed', 'Your payment of 2500 UAH has been processed successfully.',
        TRUE, '/memberships', NOW() - INTERVAL '5 days', 'SYSTEM'),

       ('ee000000-0000-0000-0000-000000000006',
        'a0000000-0000-0000-0000-000000000032', 'MEMBERSHIP_EXPIRING', 'URGENT',
        'Membership Expiring', 'Your membership expires in 3 days. Renew now to keep access.',
        FALSE, '/memberships', NOW() - INTERVAL '2 days', 'SYSTEM');
