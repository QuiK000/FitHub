import api from './api'
import type {
    ClientWorkoutPlanResponse,
    LogWorkoutRequest,
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

