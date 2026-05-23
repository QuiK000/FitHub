import type { ISODateString, ISODateTimeString } from './common.types'
import type { ClientShortResponse, TrainerShortResponse } from './user.types'

export type ClientWorkoutStatus =
  | 'ASSIGNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED'

export type DifficultyLevel =
  | 'BEGINNER'
  | 'INTERMEDIATE'
  | 'ADVANCED'
  | 'EXPERT'

export type ExerciseCategory =
  | 'STRENGTH'
  | 'CARDIO'
  | 'FLEXIBILITY'
  | 'BALANCE'
  | 'PLYOMETRIC'
  | 'OLYMPIC_LIFTING'
  | 'POWERLIFTING'
  | 'CALISTHENICS'
  | 'STRETCHING'
  | 'YOGA'
  | 'PILATES'

export type MuscleGroup =
  | 'CHEST'
  | 'BACK'
  | 'SHOULDERS'
  | 'BICEPS'
  | 'TRICEPS'
  | 'FOREARMS'
  | 'CORE'
  | 'ABS'
  | 'OBLIQUES'
  | 'LOWER_BACK'
  | 'QUADS'
  | 'HAMSTRINGS'
  | 'GLUTES'
  | 'CALVES'
  | 'FULL_BODY'

export type TrainingStatus = 'SCHEDULED' | 'CANCELLED' | 'COMPLETED'
export type TrainingType = 'GROUP' | 'PERSONAL'
export type WaitlistStatus = 'WAITING' | 'PROMOTED' | 'CANCELLED' | 'EXPIRED'

export interface CreateWorkoutPlanRequest {
  name: string
  description: string
  difficultyLevel: DifficultyLevel
  durationWeeks: number
  sessionsPerWeek: number
}

export type UpdateWorkoutPlanRequest = Partial<CreateWorkoutPlanRequest>

export interface WorkoutExerciseDetailsRequest {
  exerciseId: string
  dayNumber: number
  orderIndex: number
  sets?: number
  reps?: number
  durationSeconds?: number
  restSeconds?: number
  notes?: string
}

export type AddExerciseToPlanRequest = WorkoutExerciseDetailsRequest

export interface AssignWorkoutPlanRequest {
  clientId: string
  startDate: ISODateTimeString
}

export interface ReorderWorkoutPlanExerciseItem {
  planExerciseId: string
  orderIndex: number
}

export interface ReorderWorkoutPlanExerciseRequest {
  day: number
  exercises: ReorderWorkoutPlanExerciseItem[]
}

export interface UpdatePlanExerciseRequest {
  sets?: number
  reps?: number
  durationSeconds?: number
  restSeconds?: number
  notes?: string
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

export interface UpdateLogWorkoutRequest {
  setsCompleted?: number
  repsCompleted?: number
  weightUsed?: number
  durationSeconds?: number
  difficultRating?: number
  notes?: string
}

export interface CreateExerciseRequest {
  name: string
  description: string
  category: ExerciseCategory
  primaryMuscleGroup: MuscleGroup
  secondaryMuscleGroups: MuscleGroup[]
  videoUrl?: string
  imageUrl?: string
  instructions?: string
}

export type UpdateExerciseRequest = Partial<CreateExerciseRequest>

export interface CreateTrainingSessionRequest {
  type: TrainingType
  startTime: ISODateTimeString
  endTime: ISODateTimeString
  maxParticipants: number
}

export interface UpdateTrainingSessionRequest {
  starTime?: ISODateTimeString
  endTime?: ISODateTimeString
  maxParticipants?: number
}

export interface CheckInTrainingSessionRequest {
  clientId: string
}

export interface ExerciseShortResponse {
  exerciseId: string
  name: string
  category: ExerciseCategory
  primaryMuscleGroup: MuscleGroup
  imageUrl: string | null
}

export interface ExerciseResponse {
  id: string
  name: string
  description: string
  category: ExerciseCategory
  primaryMuscleGroup: MuscleGroup
  secondaryMuscleGroups: MuscleGroup[]
  videoUrl: string | null
  imageUrl: string | null
  instructions: string | null
  active: boolean
  createdAt: ISODateTimeString
}

export interface WorkoutPlanShortResponse {
  id: string
  name: string
  difficultyLevel: DifficultyLevel
  durationWeeks: number
  sessionsPerWeek: number
  trainer: TrainerShortResponse
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
  createdAt: ISODateTimeString
}

export interface ClientWorkoutPlanResponse {
  id: string
  workoutPlan: WorkoutPlanShortResponse
  assignedDate: ISODateTimeString
  startDate: ISODateTimeString | null
  endDate: ISODateTimeString | null
  status: ClientWorkoutStatus
  completionPercentage: number | null
  totalWorkouts: number | null
  completedWorkouts: number | null
}

export interface WorkoutLogResponse {
  id: string
  exercise: ExerciseShortResponse
  workoutDate: ISODateTimeString
  setsCompleted: number | null
  repsCompleted: number | null
  weightUsed: number | null
  durationSeconds: number | null
  difficultyRating: number | null
  notes: string | null
}

export interface SessionShortResponse {
  sessionId: string
  sessionStartTime: ISODateTimeString
  sessionEndTime: ISODateTimeString
}

export interface TrainingSessionResponse {
  id: string
  type: TrainingType
  status: TrainingStatus
  startTime: ISODateTimeString
  endTime: ISODateTimeString
  maxParticipants: number
  currentParticipants: number
  trainer: TrainerShortResponse
}

export interface CheckInResponse {
  success: boolean
  clientId: string
  sessionId: string
  checkInTime: ISODateTimeString
  message: string
}

export interface AttendanceResponse {
  id: string
  checkInTime: ISODateTimeString
  session: SessionShortResponse
  trainer: TrainerShortResponse
}

export interface AttendanceSessionResponse {
  id: string
  checkInTime: ISODateTimeString
  client: ClientShortResponse
}

export interface AttendanceStatsResponse {
  date: ISODateString
  checkIns: number
}

export interface WaitlistResponse {
  id: string
  position: number
  status: WaitlistStatus
  joinedAt: ISODateTimeString
  client: ClientShortResponse
  estimatedWait: string
  message: string
}
