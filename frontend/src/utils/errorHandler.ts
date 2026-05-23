import axios, { AxiosError } from 'axios'
import i18n from '../i18n/config'
import toast from './toast'

export interface ApiError {
  message: string
  code?: string
  status?: number
  details?: Record<string, unknown>
}

const authMessageByStatus: Record<number, string> = {
  400: 'Please check the form and try again.',
  401: 'Your email or password is incorrect.',
  403: 'Your account cannot access this area.',
  404: 'We could not find a matching account.',
  409: 'This account already exists.',
  422: 'Some fields need attention before continuing.',
  429: 'Too many attempts. Please wait a moment and try again.',
}

export const getApiErrorMessage = (
  error: unknown,
  fallback = i18n.t('common:errors.error'),
) => {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status
    const responseData = error.response?.data as
      | { message?: string; error?: string }
      | undefined

    if (!error.response) {
      return i18n.t('common:errors.networkError')
    }

    if (status && authMessageByStatus[status]) {
      return authMessageByStatus[status]
    }

    return responseData?.message || responseData?.error || fallback
  }

  return error instanceof Error ? error.message : fallback
}

export const handleApiError = (error: unknown): ApiError => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string; error?: string }>

    // Network error
    if (!axiosError.response) {
      const message = i18n.t('common:errors.networkError')
      toast.error(message)
      return {
        message,
        code: 'NETWORK_ERROR',
      }
    }

    const status = axiosError.response.status
    const responseData = axiosError.response.data

    // Extract error message
    let message = getApiErrorMessage(error, axiosError.message)

    // Handle specific status codes
    switch (status) {
      case 401:
        message = i18n.t('common:errors.unauthorized')
        // Optionally trigger logout
        break
      case 403:
        message = i18n.t('common:errors.forbidden')
        break
      case 404:
        message = message || 'Resource not found'
        break
      case 500:
      case 502:
      case 503:
        message = i18n.t('common:errors.serverError')
        break
      default:
        message = message || i18n.t('common:errors.error')
    }

    toast.error(message)

    return {
      message,
      code: axiosError.code,
      status,
      details: responseData as Record<string, unknown>,
    }
  }

  // Non-Axios error
  const message = error instanceof Error ? error.message : i18n.t('common:errors.error')
  toast.error(message)

  return {
    message,
    code: 'UNKNOWN_ERROR',
  }
}

export const handleApiSuccess = (message: string, description?: string) => {
  toast.success(message, { description })
}

export default {
  handleApiError,
  handleApiSuccess,
}
