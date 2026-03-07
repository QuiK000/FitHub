import api from './api'

export interface ClientProfileResponse {
  firstname: string
  lastname: string
  phone: string
  birthdate: string | null
  height: number | null
  weight: number | null
  dailyWaterTarget: number | null
  gender: string | null
  active: boolean
  createdAt: string
}

export interface UserResponse {
  id: string
  email: string
  enabled: boolean
  roles: string[]
  trainerProfile?: unknown | null
  clientProfile?: ClientProfileResponse | null
}

export const getCurrentUser = async (): Promise<UserResponse> => {
  const { data } = await api.get<UserResponse>('/users/me')
  return data
}

