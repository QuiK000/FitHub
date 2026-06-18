import api from './api'
import type {
    ClientProfileResponse,
    CreateClientProfileRequest,
    CreateTrainerProfileRequest,
    TrainerProfileResponse,
    UpdateClientProfileRequest,
    UpdateTrainerProfileRequest,
} from '../types/user.types'
import type { MessageResponse } from '../types/common.types'

export type {
    ClientGender,
    ClientProfileResponse,
    CreateClientProfileRequest,
    CreateTrainerProfileRequest,
    TrainerProfileResponse,
    UpdateClientProfileRequest,
    UpdateTrainerProfileRequest,
} from '../types/user.types'

export const getMyClientProfile = async (): Promise<ClientProfileResponse> => {
    const {data} = await api.get<ClientProfileResponse>('/profile/client/me')
    return data
}

export const createClientProfile = async (
    payload: CreateClientProfileRequest,
): Promise<ClientProfileResponse> => {
    const {data} = await api.post<ClientProfileResponse>('/profile/client', payload)
    return data
}

export const updateMyClientProfile = async (
    payload: UpdateClientProfileRequest,
): Promise<MessageResponse> => {
    const {data} = await api.put<MessageResponse>('/profile/client/me', payload)
    return data
}

export const deactivateClientProfile = async (): Promise<MessageResponse> => {
    const {data} = await api.patch<MessageResponse>('/profile/client/me/deactivate')
    return data
}

export const clearClientProfile = async (): Promise<ClientProfileResponse> => {
    const {data} = await api.patch<ClientProfileResponse>('/profile/client/me/clear')
    return data
}

export const getMyTrainerProfile = async (): Promise<TrainerProfileResponse> => {
    const {data} = await api.get<TrainerProfileResponse>('/profile/trainer/me')
    return data
}

export const createTrainerProfile = async (
    payload: CreateTrainerProfileRequest,
): Promise<TrainerProfileResponse> => {
    const {data} = await api.post<TrainerProfileResponse>('/profile/trainer', payload)
    return data
}

export const updateMyTrainerProfile = async (
    payload: UpdateTrainerProfileRequest,
): Promise<TrainerProfileResponse> => {
    const {data} = await api.put<TrainerProfileResponse>('/profile/trainer/me', payload)
    return data
}

export const deactivateTrainerProfile = async (): Promise<MessageResponse> => {
    const {data} = await api.patch<MessageResponse>('/profile/trainer/me/deactivate')
    return data
}

export const clearTrainerProfile = async (): Promise<TrainerProfileResponse> => {
    const {data} = await api.patch<TrainerProfileResponse>('/profile/trainer/me/clear')
    return data
}
