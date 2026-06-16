import api from './api'
import type { PageResponse } from '../types'
import type {
  TrainerReviewResponse,
  TrainerReviewSummaryResponse,
  CreateTrainerReviewRequest,
  UpdateTrainerReviewRequest,
} from '../types'
import type { TrainerProfileResponse } from '../types'

export type {
  TrainerReviewResponse,
  TrainerReviewSummaryResponse,
  CreateTrainerReviewRequest,
  UpdateTrainerReviewRequest,
} from '../types/review.types'

export type { TrainerProfileResponse } from '../types/user.types'

export const getTrainers = async (
  page = 0,
  size = 10,
  search?: string,
): Promise<PageResponse<TrainerProfileResponse>> => {
  const { data } = await api.get<PageResponse<TrainerProfileResponse>>(
    '/profile/trainer',
    { params: { page, size, search } },
  )
  return data
}

export const getTrainerReviews = async (
  trainerId: string,
  page = 0,
  size = 10,
): Promise<PageResponse<TrainerReviewResponse>> => {
  const { data } = await api.get<PageResponse<TrainerReviewResponse>>(
    `/reviews/trainers/${trainerId}`,
    { params: { page, size } },
  )
  return data
}

export const getTrainerReviewSummary = async (): Promise<TrainerReviewSummaryResponse | null> => {
  try {
    const { data } = await api.get<TrainerReviewSummaryResponse>(
      '/reviews/me/summary',
    )
    return data
  } catch {
    return null
  }
}

export const createTrainerReview = async (
  trainerId: string,
  payload: CreateTrainerReviewRequest,
): Promise<TrainerReviewResponse> => {
  const { data } = await api.post<TrainerReviewResponse>(
    `/reviews/trainers/${trainerId}`,
    payload,
  )
  return data
}

export const updateTrainerReview = async (
  reviewId: string,
  payload: UpdateTrainerReviewRequest,
): Promise<void> => {
  await api.put(`/reviews/${reviewId}`, payload)
}

export const getMyReviews = async (
  page = 0,
  size = 10,
): Promise<PageResponse<TrainerReviewResponse>> => {
  const { data } = await api.get<PageResponse<TrainerReviewResponse>>(
    '/reviews/my-reviews',
    { params: { page, size } },
  )
  return data
}

export const getMyTrainerReviews = async (
  page = 0,
  size = 10,
): Promise<PageResponse<TrainerReviewResponse>> => {
  const { data } = await api.get<PageResponse<TrainerReviewResponse>>(
    '/reviews/me',
    { params: { page, size } },
  )
  return data
}
