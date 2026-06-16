import api from './api'
import type { PageResponse } from '../types'
import type {
  NotificationResponse,
  NotificationSummaryResponse,
} from '../types'

export type {
  NotificationResponse,
  NotificationSummaryResponse,
  NotificationType,
  NotificationPriority,
} from '../types/notification.types'

export const getNotifications = async (
  page = 0,
  size = 10,
): Promise<PageResponse<NotificationResponse>> => {
  const { data } = await api.get<PageResponse<NotificationResponse>>(
    '/notifications',
    { params: { page, size } },
  )
  return data
}

export const getNotificationSummary =
  async (): Promise<NotificationSummaryResponse> => {
    const { data } = await api.get<NotificationSummaryResponse>(
      '/notifications/summary',
    )
    return data
  }

export const getUnreadCount = async (): Promise<number> => {
  const { data } = await api.get<number>('/notifications/unread/count')
  return data
}

export const getNotificationById = async (
  notificationId: string,
): Promise<NotificationResponse> => {
  const { data } = await api.get<NotificationResponse>(
    `/notifications/${notificationId}`,
  )
  return data
}

export const markAsRead = async (
  notificationId: string,
): Promise<void> => {
  await api.patch(`/notifications/${notificationId}/read`)
}

export const markAllAsRead = async (): Promise<void> => {
  await api.patch('/notifications/mark-all-read')
}

export const sendNotification = async (payload: {
  recipientId: string
  type: string
  priority: string
  title: string
  message: string
  actionUrl?: string
}): Promise<void> => {
  await api.post('/notifications/send', payload)
}

export const getScheduledNotifications = async (
  page = 0,
  size = 10,
): Promise<PageResponse<NotificationResponse>> => {
  const { data } = await api.get<PageResponse<NotificationResponse>>(
    '/notifications/scheduled',
    { params: { page, size } },
  )
  return data
}

export const broadcastNotification = async (payload: {
  title: string
  message: string
  priority: string
  type: string
}): Promise<void> => {
  await api.post('/notifications/broadcast', payload)
}
