import api from './api'
import type { PageResponse } from '../types/common.types'
import type {
    ClientWorkoutPlanResponse,
    LogWorkoutRequest,
    TrainingSessionResponse,
    WorkoutLogResponse,
    WorkoutPlanResponse,
} from '../types/workout.types'

export type {
    AddExerciseToPlanRequest,
    AssignWorkoutPlanRequest,
    AttendanceResponse,
    AttendanceSessionResponse,
    AttendanceStatsResponse,
    CheckInResponse,
    CheckInTrainingSessionRequest,
    ClientWorkoutPlanResponse,
    ClientWorkoutStatus,
    CreateExerciseRequest,
    CreateTrainingSessionRequest,
    CreateWorkoutPlanRequest,
    DifficultyLevel,
    ExerciseCategory,
    ExerciseResponse,
    ExerciseShortResponse,
    LogWorkoutRequest,
    MuscleGroup,
    ReorderWorkoutPlanExerciseItem,
    ReorderWorkoutPlanExerciseRequest,
    SessionShortResponse,
    TrainingSessionResponse,
    TrainingStatus,
    TrainingType,
    UpdateExerciseRequest,
    UpdateLogWorkoutRequest,
    UpdatePlanExerciseRequest,
    UpdateTrainingSessionRequest,
    UpdateWorkoutPlanRequest,
    WaitlistResponse,
    WaitlistStatus,
    WorkoutExerciseDetailsRequest,
    WorkoutLogResponse,
    WorkoutPlanExerciseResponse,
    WorkoutPlanResponse,
    WorkoutPlanShortResponse,
} from '../types/workout.types'

export const getMyActiveAssignments =
    async (): Promise<ClientWorkoutPlanResponse[]> => {
        try {
            const {data} = await api.get<ClientWorkoutPlanResponse[]>(
                '/workout-plans/my-assignments/active',
            )
            return data
        } catch (error) {
            if ((error as { response?: { status?: number } }).response?.status === 404) {
                const {data} = await api.get<ClientWorkoutPlanResponse[]>(
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
    const {data} = await api.get<ClientWorkoutPlanResponse>(
        `/workout-plans/my-assignments/${assignmentId}`,
    )
    return data
}

export const getMyAssignments =
    async (): Promise<ClientWorkoutPlanResponse[]> => {
        const {data} = await api.get<ClientWorkoutPlanResponse[]>(
            '/workout-plans/my-assignments',
        )
        return data
    }

export const getWorkoutPlanById = async (
    workoutPlanId: string,
): Promise<WorkoutPlanResponse> => {
    const {data} = await api.get<WorkoutPlanResponse>(
        `/workout-plans/${workoutPlanId}`,
    )
    return data
}

export const logWorkout = async (
    payload: LogWorkoutRequest,
): Promise<WorkoutLogResponse> => {
    const {data} = await api.post<WorkoutLogResponse>(
        '/workout-logs',
        payload,
    )
    return data
}

export const getMyWorkoutLogs = async (
    page = 0,
    size = 10,
): Promise<PageResponse<WorkoutLogResponse>> => {
    const {data} = await api.get<PageResponse<WorkoutLogResponse>>(
        '/workout-logs/my-logs',
        {
            params: {page, size},
        },
    )
    return data
}

export const getTrainingSessions = async (
    page = 0,
    size = 10,
    search?: string,
): Promise<PageResponse<TrainingSessionResponse>> => {
    const {data} = await api.get<PageResponse<TrainingSessionResponse>>(
        '/sessions',
        {
            params: {page, size, search},
        },
    )
    return data
}
