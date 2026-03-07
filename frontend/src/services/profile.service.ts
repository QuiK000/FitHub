import api from './api'
import type { ClientProfileResponse } from './user.service'

export const getMyClientProfile =
  async (): Promise<ClientProfileResponse> => {
    const { data } = await api.get<ClientProfileResponse>('/profile/client/me')
    return data
  }

