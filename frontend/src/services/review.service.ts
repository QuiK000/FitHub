import api from './api'
import type { PageResponse } from '../types'
import type {
  TrainerReviewResponse,
  AdminReviewFilterRequest,
  UpdateReviewVisibilityRequest,
} from '../types'

export type {
  TrainerReviewResponse,
  AdminReviewFilterRequest,
  UpdateReviewVisibilityRequest,
  CreateTrainerReviewRequest,
  UpdateTrainerReviewRequest,
} from '../types/review.types'

export const getAllReviews = async (
  page = 0,
  size = 10,
  filters?: AdminReviewFilterRequest,
): Promise<PageResponse<TrainerReviewResponse>> => {
  const params: Record<string, unknown> = { page, size }
  if (filters?.trainerId) params.trainerId = filters.trainerId
  if (filters?.reviewerId) params.reviewerId = filters.reviewerId
  if (filters?.isVisible !== undefined) params.isVisible = filters.isVisible
  if (filters?.rating) params.rating = filters.rating

  const { data } = await api.get<PageResponse<TrainerReviewResponse>>(
    '/reviews',
    { params },
  )
  return data
}

export const updateReviewVisibility = async (
  reviewId: string,
  payload: UpdateReviewVisibilityRequest,
): Promise<void> => {
  await api.patch(`/reviews/${reviewId}/visibility`, payload)
}
