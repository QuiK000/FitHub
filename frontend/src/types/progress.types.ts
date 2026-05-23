import type { ISODateTimeString } from './common.types'
import type { ExerciseShortResponse } from './workout.types'

export type GoalStatus = 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'ON_HOLD'
export type GoalType = 'WEIGHT_LOSS' | 'MUSCLE_GAIN' | 'ENDURANCE' | 'STRENGTH'
export type MeasurementUnit = 'KG' | 'CM' | 'MINUTES' | 'REPS'
export type PersonalRecordUnit = 'KG' | 'REPS' | 'SECONDS' | 'METERS'
export type PhotoAngle = 'FRONT' | 'BACK' | 'SIDE_LEFT' | 'SIDE_RIGHT'
export type RecordType = 'MAX_WEIGHT' | 'MAX_REPS' | 'MAX_DISTANCE' | 'BEST_TIME'

export type MeasurementType =
  | 'NECK'
  | 'CHEST'
  | 'WAIST'
  | 'HIPS'
  | 'THIGH_LEFT'
  | 'THIGH_RIGHT'
  | 'CALF_LEFT'
  | 'CALF_RIGHT'
  | 'BICEP_LEFT'
  | 'BICEP_RIGHT'
  | 'FOREARM_LEFT'
  | 'FOREARM_RIGHT'
  | 'SHOULDERS'

export type MeasurementMap = Partial<Record<MeasurementType, number>>

export interface CreateBodyMeasurementRequest {
  measurementDate?: ISODateTimeString
  weight?: number
  bodyFatPercentage?: number
  muscleMass?: number
  bmi?: number
  bmr?: number
  bodyWaterPercentage?: number
  boneMass?: number
  visceralFatLevel?: number
  measurements?: MeasurementMap
  notes?: string
  photoUrl?: string
}

export type UpdateBodyMeasurementRequest = Omit<
  CreateBodyMeasurementRequest,
  'measurementDate'
>

export interface CreateGoalRequest {
  title: string
  description?: string
  goalType: GoalType
  startValue?: number
  targetValue: number
  currentValue?: number
  unit: MeasurementUnit
  targetDate?: ISODateTimeString
  notes?: string
}

export type UpdateGoalRequest = Partial<CreateGoalRequest>

export interface UpdateGoalProgressRequest {
  currentValue: number
  notes?: string
}

export interface CreatePersonalRecordRequest {
  exerciseId: string
  recordType: RecordType
  value: number
  unit: PersonalRecordUnit
  notes?: string
  videoUrl?: string
}

export interface CreateProgressPhotoRequest {
  photoUrl: string
  angle: PhotoAngle
  notes?: string
  measurementId?: string
}

export interface BodyMeasurementResponse {
  id: string
  measurementDate: ISODateTimeString
  weight: number | null
  bodyFatPercentage: number | null
  muscleMass: number | null
  bmi: number | null
  bmr: number | null
  bodyWaterPercentage: number | null
  boneMass: number | null
  visceralFatLevel: number | null
  measurements: MeasurementMap | null
  notes: string | null
  photoUrl: string | null
  weightChange: number | null
  bodyFatChange: number | null
  muscleMassChange: number | null
}

export interface BodyMeasurementShortResponse {
  id: string
  measurementDate: ISODateTimeString
  weight: number | null
  bodyFatPercentage: number | null
}

export interface MeasurementTrendsDto {
  totalWeightChange: number | null
  totalBodyFatChange: number | null
  totalMuscleMassChange: number | null
  measurementCount: number
  daysSinceFirst: number
}

export interface MeasurementHistoryResponse {
  measurements: BodyMeasurementResponse[]
  trends: MeasurementTrendsDto
}

export interface GoalResponse {
  id: string
  title: string
  description: string | null
  goalType: GoalType
  targetValue: number
  currentValue: number
  startValue: number
  unit: MeasurementUnit
  startDate: ISODateTimeString
  targetDate: ISODateTimeString | null
  completionDate: ISODateTimeString | null
  status: GoalStatus
  progressPercentage: number
  notes: string | null
  daysRemaining: number | null
  averageProgressPerDay: number | null
}

export interface PersonalRecordResponse {
  id: string
  exercise: ExerciseShortResponse
  recordType: RecordType
  value: number
  unit: PersonalRecordUnit
  recordDate: ISODateTimeString
  previousRecord: number | null
  improvement: number | null
  notes: string | null
  videoUrl: string | null
}

export interface ProgressPhotoResponse {
  id: string
  photoDate: ISODateTimeString
  photoUrl: string
  angle: PhotoAngle
  notes: string | null
  measurement: BodyMeasurementShortResponse | null
}
