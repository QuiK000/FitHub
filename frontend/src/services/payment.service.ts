import api from './api'
import type { PageResponse } from '../types'
import type { PaymentResponse, CreatePaymentRequest } from '../types'

export type { PaymentResponse, CreatePaymentRequest } from '../types/membership.types'

export const createPayment = async (
  payload: CreatePaymentRequest,
): Promise<PaymentResponse> => {
  const { data } = await api.post<PaymentResponse>('/payments', payload)
  return data
}

export const getMyPayments = async (
  page = 0,
  size = 10,
): Promise<PageResponse<PaymentResponse>> => {
  const { data } = await api.get<PageResponse<PaymentResponse>>(
    '/payments/me',
    { params: { page, size } },
  )
  return data
}

export const getPaymentsByClientId = async (
  clientId: string,
  page = 0,
  size = 10,
): Promise<PageResponse<PaymentResponse>> => {
  const { data } = await api.get<PageResponse<PaymentResponse>>(
    `/payments/client/${clientId}`,
    { params: { page, size } },
  )
  return data
}
