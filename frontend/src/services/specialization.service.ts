import api from './api'
import type { PageResponse } from '../types'
import type { SpecializationResponse } from '../types'

export type { SpecializationResponse } from '../types/user.types'

export const getSpecializations = async (
  page = 0,
  size = 50,
): Promise<PageResponse<SpecializationResponse>> => {
  const { data } = await api.get<PageResponse<SpecializationResponse>>(
    '/specializations',
    { params: { page, size } },
  )
  return data
}

export const createSpecialization = async (payload: {
  name: string
  description: string
}): Promise<SpecializationResponse> => {
  const { data } = await api.post<SpecializationResponse>(
    '/specializations',
    payload,
  )
  return data
}

export const updateSpecialization = async (
  specializationId: string,
  payload: { name?: string; description?: string },
): Promise<SpecializationResponse> => {
  const { data } = await api.put<SpecializationResponse>(
    `/specializations/${specializationId}`,
    payload,
  )
  return data
}

export const disableSpecialization = async (
  specializationId: string,
): Promise<void> => {
  await api.patch(`/specializations/${specializationId}/disable`)
}
