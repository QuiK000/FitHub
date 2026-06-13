import api from './api'
import type { PageResponse } from '../types/common.types'
import type { ClientProfileResponse, UserResponse } from '../types/user.types'

export type { ClientProfileResponse, UserResponse } from '../types/user.types'

export const getCurrentUser = async (): Promise<UserResponse> => {
  const { data } = await api.get<UserResponse>('/users/me')
  return data
}

export const searchClients = async (
  search: string,
  page = 0,
  size = 20,
): Promise<PageResponse<ClientProfileResponse>> => {
  const { data } = await api.get<PageResponse<ClientProfileResponse>>(
    '/profile/client',
    { params: { page, size, search } },
  )
  return data
}

export const getUserById = async (
  userId: string,
): Promise<UserResponse> => {
  const { data } = await api.get<UserResponse>(
    `/users/${userId}`,
  )
  return data
}

export const getAllClients = async (
  page = 0,
  size = 20,
  search?: string,
): Promise<PageResponse<ClientProfileResponse>> => {
  const { data } = await api.get<PageResponse<ClientProfileResponse>>(
    '/profile/client',
    { params: { page, size, search } },
  )
  return data
}
