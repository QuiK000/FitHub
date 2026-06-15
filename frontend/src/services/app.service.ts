import api from './api'
import type { AppBootstrapResponse } from '../types'

export const getBootstrap = async (): Promise<AppBootstrapResponse> => {
  const { data } = await api.get<AppBootstrapResponse>('/app/bootstrap')
  return data
}
