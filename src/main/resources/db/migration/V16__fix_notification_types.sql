-- ============================================================
-- V16: Fix incorrect notification_type values in notifications
-- ============================================================
-- V15 used types that don't exist in NotificationType enum.
-- Mapping applied:
--   PAYMENT_CONFIRMED   → PAYMENT_SUCCESS
--   WORKOUT_ASSIGNED    → NEW_WORKOUT_PLAN
--   PERSONAL_RECORD     → MILESTONE_REACHED
--   GOAL_PROGRESS       → MILESTONE_REACHED
--   SESSION_COMPLETED   → WORKOUT_COMPLETED
--   WATER_INTAKE_REMINDER → PROFILE_UPDATE_REMINDER
--   NEW_CLIENT          → GENERAL_ANNOUNCEMENT
--   WELCOME             → GENERAL_ANNOUNCEMENT
-- ============================================================

UPDATE notifications
SET notification_type = 'PAYMENT_SUCCESS'
WHERE notification_type = 'PAYMENT_CONFIRMED';

UPDATE notifications
SET notification_type = 'NEW_WORKOUT_PLAN'
WHERE notification_type = 'WORKOUT_ASSIGNED';

UPDATE notifications
SET notification_type = 'MILESTONE_REACHED'
WHERE notification_type = 'PERSONAL_RECORD';

UPDATE notifications
SET notification_type = 'MILESTONE_REACHED'
WHERE notification_type = 'GOAL_PROGRESS';

UPDATE notifications
SET notification_type = 'WORKOUT_COMPLETED'
WHERE notification_type = 'SESSION_COMPLETED';

UPDATE notifications
SET notification_type = 'PROFILE_UPDATE_REMINDER'
WHERE notification_type = 'WATER_INTAKE_REMINDER';

UPDATE notifications
SET notification_type = 'GENERAL_ANNOUNCEMENT'
WHERE notification_type = 'NEW_CLIENT';

UPDATE notifications
SET notification_type = 'GENERAL_ANNOUNCEMENT'
WHERE notification_type = 'WELCOME';