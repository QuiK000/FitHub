import axios from 'axios'
import api from './api'
import type { PageResponse } from '../types/common.types'
import type {
  BodyMeasurementResponse,
  GoalResponse,
} from '../types/progress.types'

export type {
  BodyMeasurementResponse,
  GoalResponse,
  GoalStatus,
  GoalType,
  MeasurementHistoryResponse,
} from '../types/progress.types'

export const getActiveGoals = async (
  page = 0,
  size = 3,
): Promise<PageResponse<GoalResponse>> => {
  const { data } = await api.get<PageResponse<GoalResponse>>(
    '/progress/goals/active',
    {
      params: { page, size },
    },
  )
  return data
}

export const getLatestBodyMeasurement =
  async (): Promise<BodyMeasurementResponse | null> => {
    try {
      const { data } = await api.get<BodyMeasurementResponse>(
        '/progress/measurements/latest',
      )
      return data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null
      }

      throw error
    }
  }
