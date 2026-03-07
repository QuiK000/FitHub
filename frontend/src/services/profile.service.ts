import api from './api'

export type ClientGender = 'MALE' | 'FEMALE' | 'OTHER'

export interface CreateClientProfileRequest {
    firstname: string
    lastname: string
    phone: string
    birthdate: string | null
    height: number
    weight: number
    dailyWaterTarget: number
    gender: ClientGender
}

export interface ClientProfileResponse {
    firstname: string
    lastname: string
    phone: string
    birthdate: string | null
    height: number | null
    weight: number | null
    dailyWaterTarget: number | null
    gender: ClientGender | null
    active: boolean
    createdAt: string
}

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