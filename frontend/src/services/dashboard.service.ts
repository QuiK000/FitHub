import api from './api'

export interface PopularSessionResponse {
  sessionId: string
  trainerName: string
  attendanceCount: number
}

export interface DashboardAnalyticsResponse {
  activeClients: number
  activeMemberships: number
  revenue: number
  todayCheckIns: number
  popularSessions: PopularSessionResponse[]
}

export interface AttendanceStatsResponse {
  date: string
  checkIns: number
}

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

