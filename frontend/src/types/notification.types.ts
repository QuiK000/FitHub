import type { ISODateTimeString } from './common.types'

export type NotificationPriority = 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT'

export type NotificationType =
  | 'SESSION_REMINDER'
  | 'SESSION_CANCELLED'
  | 'SESSION_RESCHEDULED'
  | 'MEMBERSHIP_EXPIRING'
  | 'MEMBERSHIP_EXPIRED'
  | 'MEMBERSHIP_ACTIVATED'
  | 'PAYMENT_SUCCESS'
  | 'PAYMENT_FAILED'
  | 'PAYMENT_REFUNDED'
  | 'NEW_WORKOUT_PLAN'
  | 'WORKOUT_COMPLETED'
  | 'GOAL_ACHIEVED'
  | 'MILESTONE_REACHED'
  | 'NEW_MESSAGE'
  | 'NEW_REVIEW'
  | 'TRAINER_ASSIGNED'
  | 'GENERAL_ANNOUNCEMENT'
  | 'MAINTENANCE_SCHEDULED'
  | 'PROFILE_UPDATE_REMINDER'
  | 'MEASUREMENT_REMINDER'

export interface CreateNotificationRequest {
  recipientId: string
  type: NotificationType
  priority: NotificationPriority
  title: string
  message: string
  actionUrl?: string
  referenceId?: string
  referenceType?: string
  scheduledFor?: ISODateTimeString
}

export type BroadcastNotificationRequest = Omit<
  CreateNotificationRequest,
  'recipientId'
>

export interface NotificationResponse {
  id: string
  type: NotificationType
  priority: NotificationPriority
  title: string
  message: string
  read: boolean
  readAt: ISODateTimeString | null
  actionUrl: string | null
  referenceId: string | null
  referenceType: string | null
  createdAt: ISODateTimeString
  timeAgo: string
}

export interface NotificationSummaryResponse {
  unreadCount: number
  recentNotifications: NotificationResponse[]
}

export interface NotificationRealtimeEventResponse {
  eventType: string
  unreadCount: number
  notification: NotificationResponse
  timestamp: ISODateTimeString
}
