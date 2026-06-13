import type { ISODateString, ISODateTimeString } from './common.types'

export type ClientGender = 'MALE' | 'FEMALE' | 'OTHER'
export type RoleName = 'CLIENT' | 'TRAINER' | 'ADMIN'

export interface CreateClientProfileRequest {
  firstname: string
  lastname: string
  phone: string
  birthdate: ISODateString | null
  height: number
  weight: number
  dailyWaterTarget: number
  gender: ClientGender
}

export interface UpdateClientProfileRequest {
  firstname?: string
  lastname?: string
  phone?: string
  birthdate?: ISODateString | null
  height?: number
  weight?: number
  dailyWaterTarget?: number
  gender: ClientGender
}

export interface CreateSpecializationRequest {
  name: string
  description: string
}

export interface UpdateSpecializationRequest {
  name?: string
  description?: string
}

export interface CreateTrainerProfileRequest {
  firstname: string
  lastname: string
  specializationIds: string[]
  experienceYears: number
  description: string
}

export interface UpdateTrainerProfileRequest {
  firstname?: string
  lastname?: string
  specializationIds?: string[]
  experienceYears?: number
  description?: string
}

export interface ClientProfileResponse {
  id?: string
  firstname: string
  lastname: string
  phone: string
  birthdate: ISODateString | null
  height: number | null
  weight: number | null
  dailyWaterTarget: number | null
  gender: ClientGender | null
  active: boolean
  createdAt: ISODateTimeString
}

export interface ClientShortResponse {
  clientId: string
  clientFirstname: string
  clientLastname: string
}

export interface ClientAnalyticsResponse {
  clientId: string
  fullName: string
  totalVisits: number
  missedSessions: number
  lastVisit: ISODateTimeString | null
}

export interface TrainerAnalyticsResponse {
  trainerId: string
  trainerName: string
  totalSessions: number
  totalClients: number
  attendanceRate: number
}

export interface SpecializationResponse {
  id: string
  name: string
  description: string
}

export interface TrainerProfileResponse {
  id: string
  firstname: string
  lastname: string
  specializations: string[]
  experienceYears: number
  description: string
  createdAt: ISODateTimeString
}

export interface TrainerShortResponse {
  trainerId: string
  firstname: string
  lastname: string
}

export interface TrainerAttendanceMetrics {
  totalSessions: number
  totalClients: number
  attendanceRate: number
}

export interface TrainerAnalyticsResponse {
  trainerId: string
  trainerName: string
  totalSessions: number
  totalClients: number
  attendanceRate: number
}

export interface UserResponse {
  id: string
  email: string
  enabled: boolean
  roles: RoleName[]
  trainerProfile: TrainerProfileResponse | null
  clientProfile: ClientProfileResponse | null
}
