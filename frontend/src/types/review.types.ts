import type { ISODateTimeString } from './common.types'
import type { ClientShortResponse } from './user.types'

export interface AdminReviewFilterRequest {
  trainerId?: string
  reviewerId?: string
  isVisible?: boolean
  rating?: number
}

export interface CreateTrainerReviewRequest {
  rating: number
  comment?: string
  professionalismRating?: number
  knowledgeRating?: number
  communicationRating?: number
  motivationRating?: number
}

export type UpdateTrainerReviewRequest = Partial<CreateTrainerReviewRequest>

export interface UpdateReviewVisibilityRequest {
  visible: boolean
  reason?: string
}

export interface RatingDistributionDto {
  fiveStars: number
  fourStars: number
  threeStars: number
  twoStars: number
  oneStar: number
}

export interface TrainerReviewResponse {
  id: string
  reviewer: ClientShortResponse
  rating: number
  comment: string | null
  professionalismRating: number | null
  knowledgeRating: number | null
  communicationRating: number | null
  motivationRating: number | null
  createdAt: ISODateTimeString
  edited: boolean
  editedAt: ISODateTimeString | null
  visible: boolean
  hiddenReason: string | null
  moderatedByAdminId: string | null
  moderatedAt: ISODateTimeString | null
}

export interface TrainerReviewSummaryResponse {
  trainerId: string
  averageRating: number | null
  totalReviews: number
  professionalismAverage: number | null
  knowledgeAverage: number | null
  communicationAverage: number | null
  motivationAverage: number | null
  ratingDistribution: RatingDistributionDto
  recentReviews: TrainerReviewResponse[]
}
