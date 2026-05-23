import type { ISODateString } from './common.types'

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
  date: ISODateString
  checkIns: number
}
