import axios from 'axios'
import api from './api'
import type { PageResponse } from '../types/common.types'
import type {
  CreateMealPlanRequest,
  CreateMealRequest,
  DailyWaterIntakeResponse,
  FoodResponse,
  LogWaterIntakeRequest,
  MealPlanResponse,
  MealResponse,
  UpdateMealPlanRequest,
  WaterIntakeResponse,
} from '../types/nutrition.types'

export type {
  CreateMealPlanRequest,
  CreateMealRequest,
  DailyWaterIntakeResponse,
  FoodResponse,
  LogWaterIntakeRequest,
  MealResponse,
  MealPlanResponse,
  WaterIntakeResponse,
} from '../types/nutrition.types'

export const getTodayWaterIntake =
  async (): Promise<DailyWaterIntakeResponse | null> => {
    try {
      const { data } = await api.get<DailyWaterIntakeResponse>(
        '/nutrition/water-intake/today',
      )
      return data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null
      }

      throw error
    }
  }

export const getTodayMealPlan = async (
  date: string,
): Promise<MealPlanResponse | null> => {
  try {
    const { data } = await api.get<MealPlanResponse>(
      `/nutrition/meal-plans/date/${date}`,
    )
    return data
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null
    }

    throw error
  }
}

export const createMealPlan = async (
  payload: CreateMealPlanRequest,
): Promise<MealPlanResponse> => {
  const { data } = await api.post<MealPlanResponse>(
    '/nutrition/meal-plans',
    payload,
  )
  return data
}

export const updateMealPlan = async (
  planId: string,
  payload: UpdateMealPlanRequest,
): Promise<MealPlanResponse> => {
  const { data } = await api.put<MealPlanResponse>(
    `/nutrition/meal-plans/${planId}`,
    payload,
  )
  return data
}

export const getMyMealPlans = async (
  page = 0,
  size = 5,
): Promise<PageResponse<MealPlanResponse>> => {
  const { data } = await api.get<PageResponse<MealPlanResponse>>(
    '/nutrition/meal-plans',
    {
      params: { page, size },
    },
  )
  return data
}

export const getWeeklyWaterIntake = async (): Promise<
  DailyWaterIntakeResponse[]
> => {
  const { data } = await api.get<DailyWaterIntakeResponse[]>(
    '/nutrition/water-intake/weekly',
  )
  return data
}

export const logWaterIntake = async (
  payload: LogWaterIntakeRequest,
): Promise<WaterIntakeResponse> => {
  const { data } = await api.post<WaterIntakeResponse>(
    '/nutrition/water-intake',
    payload,
  )
  return data
}

export const addMealToPlan = async (
  planId: string,
  payload: CreateMealRequest,
): Promise<MealResponse> => {
  const { data } = await api.post<MealResponse>(
    `/nutrition/meal-plans/${planId}/meals`,
    payload,
  )
  return data
}

export const updateMeal = async (
  mealId: string,
  payload: CreateMealRequest,
): Promise<MealResponse> => {
  const { data } = await api.put<MealResponse>(
    `/nutrition/meals/${mealId}`,
    payload,
  )
  return data
}

export const completeMeal = async (
  mealId: string,
): Promise<MealResponse> => {
  const { data } = await api.patch<MealResponse>(
    `/nutrition/meals/${mealId}/complete`,
  )
  return data
}

export const searchFoods = async (
  query: string,
): Promise<FoodResponse[]> => {
  const { data } = await api.get<FoodResponse[]>(
    '/nutrition/foods/search',
    { params: { q: query } },
  )
  return data
}

export const getFoods = async (
  page = 0,
  size = 50,
): Promise<PageResponse<FoodResponse>> => {
  const { data } = await api.get<PageResponse<FoodResponse>>(
    '/nutrition/foods',
    { params: { page, size } },
  )
  return data
}

export const getFoodByBarcode = async (
  barcode: string,
): Promise<FoodResponse | null> => {
  try {
    const { data } = await api.get<FoodResponse>(
      `/nutrition/foods/barcode/${barcode}`,
    )
    return data
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null
    }
    throw error
  }
}

export const getWeeklyMealPlans = async (): Promise<MealPlanResponse[]> => {
  const { data } = await api.get<MealPlanResponse[]>(
    '/nutrition/meal-plans/weekly',
  )
  return data
}
