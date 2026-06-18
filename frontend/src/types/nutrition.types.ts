import type { ISODateString, ISODateTimeString } from './common.types'

export type MealType =
  | 'BREAKFAST'
  | 'MORNING_SNACK'
  | 'LUNCH'
  | 'AFTERNOON_SNACK'
  | 'DINNER'
  | 'EVENING_SNACK'
  | 'PRE_WORKOUT'
  | 'POST_WORKOUT'

export type ServingUnit = 'SCOOP' | 'CUP' | 'OUNCE' | 'GRAM' | 'MILLILITER'

export interface MacroNutrientsDto {
  protein: number | null
  carbs: number | null
  fats: number | null
  fiber: number | null
  sugar: number | null
}

export interface CreateFoodRequest {
  name: string
  brand?: string
  servingSize: number
  servingUnit: ServingUnit
  caloriesPerServing: number
  macrosPerServing: MacroNutrientsDto
  barcode?: string
}

export type UpdateFoodRequest = Partial<CreateFoodRequest>

export interface CreateMealPlanRequest {
  planDate: ISODateString
  targetCalories?: number
  targetMacros?: MacroNutrientsDto
  notes?: string
}

export interface UpdateMealPlanRequest {
  targetCalories?: number
  targetMacros?: MacroNutrientsDto
  notes?: string
}

export interface MealFoodRequest {
  foodId: string
  servings: number
}

export interface CreateMealRequest {
  mealType: MealType
  mealTime?: ISODateTimeString
  name: string
  description: string
  foods: MealFoodRequest[]
}

export type UpdateMealRequest = Partial<CreateMealRequest>

export interface LogWaterIntakeRequest {
  amountMl: number
}

export interface FoodResponse {
  id: string
  name: string
  brand: string | null
  servingSize: number
  servingUnit: ServingUnit
  caloriesPerServing: number
  macrosPerServing: MacroNutrientsDto
  barcode: string | null
  active: boolean
}

export interface FoodShortResponse {
  id: string
  name: string
  brand: string | null
  servingUnit: ServingUnit
}

export interface MealFoodResponse {
  id: string
  food: FoodShortResponse
  servings: number
  totalCalories: number
  totalMacros: MacroNutrientsDto
}

export interface MealResponse {
  id: string
  mealType: MealType
  mealTime: ISODateTimeString | null
  name: string | null
  description: string | null
  calories: number
  macros: MacroNutrientsDto
  foods: MealFoodResponse[]
  completed: boolean
}

export interface MealPlanResponse {
  id: string
  planDate: ISODateString
  totalCalories: number
  targetCalories: number | null
  macros: MacroNutrientsDto
  targetMacros: MacroNutrientsDto | null
  meals: MealResponse[]
  notes: string | null
  caloriesPercentage: number | null
  completed: boolean
}

export interface WaterIntakeResponse {
  id: string
  intakeDate: ISODateString
  amountMl: number
  targetMl: number
  intakeTime: ISODateTimeString
  progress: number
}

export interface DailyWaterIntakeResponse {
  date: ISODateString
  totalMl: number
  targetMl: number
  progress: number
  intakes: WaterIntakeResponse[]
}

export interface DailyCaloriesDto {
  date: ISODateString
  calories: number
  targetCalories: number
}

export interface NutritionStatisticsResponse {
  totalMealsLogged: number
  averageCalories: number
  averageMacros: MacroNutrientsDto
  currentStreak: number
  longestStreak: number
  averageWaterIntake: number
  weeklyCalories: DailyCaloriesDto[]
}
