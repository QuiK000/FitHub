import api from './api'

// Enums mirrored from backend; keep as string unions for type-safety
export type DifficultyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | string
export type ClientWorkoutStatus =
  | 'PENDING'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'
  | string

export interface TrainerShortResponse {
  id: string
  fullName: string
}

export interface WorkoutPlanShortResponse {
  id: string
  name: string
  difficultyLevel: DifficultyLevel
  durationWeeks: number
  sessionsPerWeek: number
  trainer: TrainerShortResponse
}

export interface ExerciseShortResponse {
  exerciseId: string
  name: string
  category: string
  primaryMuscleGroup: string
  imageUrl: string | null
}

export interface WorkoutPlanExerciseResponse {
  id: string
  exercise: ExerciseShortResponse
  dayNumber: number
  orderIndex: number
  sets: number | null
  reps: number | null
  durationSeconds: number | null
  restSeconds: number | null
  notes: string | null
}

export interface WorkoutPlanResponse {
  id: string
  name: string
  description: string
  difficultyLevel: DifficultyLevel
  durationWeeks: number
  sessionsPerWeek: number
  active: boolean
  trainer: TrainerShortResponse
  exercises: WorkoutPlanExerciseResponse[]
  createdAt: string
}

export interface ClientWorkoutPlanResponse {
  id: string
  workoutPlan: WorkoutPlanShortResponse
  assignedDate: string
  startDate: string | null
  endDate: string | null
  status: ClientWorkoutStatus
  completionPercentage: number | null
  totalWorkouts: number | null
  completedWorkouts: number | null
}

export interface LogWorkoutRequest {
  exerciseId: string
  clientWorkoutPlanId: string
  setsCompleted?: number
  repsCompleted?: number
  weightUsed?: number
  durationSeconds?: number
  difficultRating?: number
  notes?: string
}

export interface WorkoutLogResponse {
  id: string
  exercise: ExerciseShortResponse
  workoutDate: string
  setsCompleted: number
  repsCompleted: number
  weightUsed: number
  durationSeconds: number
  difficultyRating: number
  notes: string | null
}

export const getMyActiveAssignments =
  async (): Promise<ClientWorkoutPlanResponse[]> => {
    try {
      const { data } = await api.get<ClientWorkoutPlanResponse[]>(
          '/workout-plans/my-assignments/active',
      )
      return data
    } catch (error) {
      if ((error as { response?: { status?: number } }).response?.status === 404) {
        const { data } = await api.get<ClientWorkoutPlanResponse[]>(
            '/workout-plans/my-assignments',
        )
        return data
      }

      throw error
    }
  }

export const getMyAssignmentById = async (
  assignmentId: string,
): Promise<ClientWorkoutPlanResponse> => {
  const { data } = await api.get<ClientWorkoutPlanResponse>(
    `/workout-plans/my-assignments/${assignmentId}`,
  )
  return data
}

export const getWorkoutPlanById = async (
  workoutPlanId: string,
): Promise<WorkoutPlanResponse> => {
  const { data } = await api.get<WorkoutPlanResponse>(
    `/workout-plans/${workoutPlanId}`,
  )
  return data
}

export const logWorkout = async (
  payload: LogWorkoutRequest,
): Promise<WorkoutLogResponse> => {
  const { data } = await api.post<WorkoutLogResponse>(
    '/workout-logs',
    payload,
  )
  return data
}

