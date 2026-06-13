import api from './api'
import type { MessageResponse, PageResponse } from '../types'
import type {
    AddExerciseToPlanRequest,
    AssignWorkoutPlanRequest,
    AttendanceResponse,
    AttendanceSessionResponse,
    CheckInResponse,
    ClientWorkoutPlanResponse,
    CreateWorkoutPlanRequest,
    ExerciseResponse,
    LogWorkoutRequest,
    TrainingSessionResponse,
    UpdateWorkoutPlanRequest,
    WorkoutLogResponse,
    WorkoutPlanExerciseResponse,
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

export const joinSession = async (
    sessionId: string,
): Promise<MessageResponse> => {
    const {data} = await api.post<MessageResponse>(
        `/sessions/${sessionId}/join`,
    )
    return data
}

export const getMyAttendance =
    async (): Promise<AttendanceResponse[]> => {
        const {data} = await api.get<AttendanceResponse[]>('/attendance/me')
        return data
    }

export const getAttendanceBySession = async (
    sessionId: string,
): Promise<AttendanceSessionResponse[]> => {
    const {data} = await api.get<AttendanceSessionResponse[]>(
        `/attendance/session/${sessionId}`,
    )
    return data
}

export const getMyPlans = async (
    page = 0,
    size = 10,
): Promise<PageResponse<WorkoutPlanResponse>> => {
    const {data} = await api.get<PageResponse<WorkoutPlanResponse>>(
        '/workout-plans/my-plans',
        {params: {page, size}},
    )
    return data
}

export const createWorkoutPlan = async (
    payload: CreateWorkoutPlanRequest,
): Promise<WorkoutPlanResponse> => {
    const {data} = await api.post<WorkoutPlanResponse>(
        '/workout-plans',
        payload,
    )
    return data
}

export const updateWorkoutPlan = async (
    planId: string,
    payload: UpdateWorkoutPlanRequest,
): Promise<WorkoutPlanResponse> => {
    const {data} = await api.put<WorkoutPlanResponse>(
        `/workout-plans/${planId}`,
        payload,
    )
    return data
}

export const addExerciseToPlan = async (
    planId: string,
    payload: AddExerciseToPlanRequest,
): Promise<WorkoutPlanExerciseResponse> => {
    const {data} = await api.post<WorkoutPlanExerciseResponse>(
        `/workout-plans/${planId}/exercises`,
        payload,
    )
    return data
}

export const assignPlanToClient = async (
    planId: string,
    payload: AssignWorkoutPlanRequest,
): Promise<ClientWorkoutPlanResponse> => {
    const {data} = await api.post<ClientWorkoutPlanResponse>(
        `/workout-plans/${planId}/assign`,
        payload,
    )
    return data
}

export const getAssignedPlans = async (
    page = 0,
    size = 10,
): Promise<PageResponse<ClientWorkoutPlanResponse>> => {
    const {data} = await api.get<PageResponse<ClientWorkoutPlanResponse>>(
        '/workout-plans/assignments',
        {params: {page, size}},
    )
    return data
}

export const startAssignment = async (
    assignmentId: string,
): Promise<MessageResponse> => {
    const {data} = await api.patch<MessageResponse>(
        `/workout-plans/assignments/${assignmentId}/start`,
    )
    return data
}

export const completeAssignment = async (
    assignmentId: string,
): Promise<MessageResponse> => {
    const {data} = await api.patch<MessageResponse>(
        `/workout-plans/assignments/${assignmentId}/complete`,
    )
    return data
}

export const cancelAssignment = async (
    assignmentId: string,
): Promise<MessageResponse> => {
    const {data} = await api.patch<MessageResponse>(
        `/workout-plans/assignments/${assignmentId}/cancel`,
    )
    return data
}

export const checkInClient = async (
    sessionId: string,
    clientId: string,
): Promise<CheckInResponse> => {
    const {data} = await api.post<CheckInResponse>(
        `/sessions/${sessionId}/check-in`,
        {clientId},
    )
    return data
}

export const getExercises = async (
    page = 0,
    size = 50,
): Promise<PageResponse<ExerciseResponse>> => {
    const {data} = await api.get<PageResponse<ExerciseResponse>>(
        '/exercises',
        {params: {page, size}},
    )
    return data
}

export const getActiveExercises = async (
    page = 0,
    size = 50,
): Promise<PageResponse<ExerciseResponse>> => {
    const {data} = await api.get<PageResponse<ExerciseResponse>>(
        '/exercises/active',
        {params: {page, size}},
    )
    return data
}

export const getExercisesByCategory = async (
    category: string,
    page = 0,
    size = 50,
): Promise<PageResponse<ExerciseResponse>> => {
    const {data} = await api.get<PageResponse<ExerciseResponse>>(
        `/exercises/by-category/${category}`,
        {params: {page, size}},
    )
    return data
}

export const getExercisesByMuscleGroup = async (
    muscleGroup: string,
    page = 0,
    size = 50,
): Promise<PageResponse<ExerciseResponse>> => {
    const {data} = await api.get<PageResponse<ExerciseResponse>>(
        `/exercises/by-muscle-group/${muscleGroup}`,
        {params: {page, size}},
    )
    return data
}

export const getWorkoutLogsByDateRange = async (
    from: string,
    to: string,
    page = 0,
    size = 10,
): Promise<PageResponse<WorkoutLogResponse>> => {
    const {data} = await api.get<PageResponse<WorkoutLogResponse>>(
        '/workout-logs/date-range',
        {params: {from, to, page, size}},
    )
    return data
}

export const getWorkoutLogsByExercise = async (
    exerciseId: string,
    page = 0,
    size = 10,
): Promise<PageResponse<WorkoutLogResponse>> => {
    const {data} = await api.get<PageResponse<WorkoutLogResponse>>(
        `/workout-logs/exercise/${exerciseId}`,
        {params: {page, size}},
    )
    return data
}

export const joinWaitlist = async (
    sessionId: string,
): Promise<MessageResponse> => {
    const {data} = await api.post<MessageResponse>(
        `/sessions/${sessionId}/waitlist`,
    )
    return data
}

export const leaveWaitlist = async (
    sessionId: string,
): Promise<MessageResponse> => {
    const {data} = await api.delete<MessageResponse>(
        `/sessions/${sessionId}/waitlist`,
    )
    return data
}
