import api from './api'

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthenticationResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
}

export interface RegistrationRequest {
  email: string
  password: string
  confirmPassword: string
}

export interface MessageResponse {
  message: string
  timestamp: string
}

export const login = async (
  payload: LoginRequest,
): Promise<AuthenticationResponse> => {
  const { data } = await api.post<AuthenticationResponse>(
    '/auth/signin',
    payload,
  )
  return data
}

export const register = async (
  payload: RegistrationRequest,
): Promise<MessageResponse> => {
  const { data } = await api.post<MessageResponse>('/auth/signup', payload)
  return data
}

