import api from './api'
import type { UserResponse } from '../types/user.types'

export type { ClientProfileResponse, UserResponse } from '../types/user.types'

export const getCurrentUser = async (): Promise<UserResponse> => {
  const { data } = await api.get<UserResponse>('/users/me')
  return data
}
