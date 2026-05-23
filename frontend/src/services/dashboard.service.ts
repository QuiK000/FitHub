import api from './api'
import type {
  AttendanceStatsResponse,
  DashboardAnalyticsResponse,
} from '../types/dashboard.types'

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
