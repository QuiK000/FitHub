import api from './api'
import type {
    ClientProfileResponse,
    CreateClientProfileRequest,
} from '../types/user.types'

export type {
    ClientGender,
    ClientProfileResponse,
    CreateClientProfileRequest,
    UpdateClientProfileRequest,
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
