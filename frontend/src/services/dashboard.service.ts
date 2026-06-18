import api from './api'
import type {
  AttendanceStatsResponse,
  DashboardAnalyticsResponse,
} from '../types/dashboard.types'
import type { TrainerAnalyticsResponse, ClientAnalyticsResponse } from '../types'
import type { RevenueStatsResponse } from '../types/membership.types'

export type {
  AttendanceStatsResponse,
  DashboardAnalyticsResponse,
  PopularSessionResponse,
} from '../types/dashboard.types'

export const getDashboardAnalytics =
  async (): Promise<DashboardAnalyticsResponse> => {
    const { data } = await api.get<DashboardAnalyticsResponse>('/analytics/dashboard')
    return data
  }

export const getAttendanceStats = async (
  from: string,
  to: string,
): Promise<AttendanceStatsResponse[]> => {
  const { data } = await api.get<AttendanceStatsResponse[]>('/analytics/attendance', {
    params: { from, to },
  })
  return data
}

export const getTrainerAnalytics = async (
  trainerId: string,
): Promise<TrainerAnalyticsResponse> => {
  const { data } = await api.get<TrainerAnalyticsResponse>(
    `/analytics/trainers/${trainerId}`,
  )
  return data
}

export const getMyTrainerAnalytics =
  async (): Promise<TrainerAnalyticsResponse> => {
    const { data } = await api.get<TrainerAnalyticsResponse>(
      '/analytics/trainers/me',
    )
    return data
  }

export const getClientAnalytics = async (
  clientId: string,
): Promise<ClientAnalyticsResponse> => {
  const { data } = await api.get<ClientAnalyticsResponse>(
    `/analytics/clients/${clientId}`,
  )
  return data
}

export const getRevenueStats = async (
  from: string,
  to: string,
): Promise<RevenueStatsResponse[]> => {
  const { data } = await api.get<RevenueStatsResponse[]>(
    '/analytics/revenue',
    { params: { from, to } },
  )
  return data
}
