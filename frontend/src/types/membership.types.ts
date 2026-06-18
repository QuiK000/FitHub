import type { ISODateString, ISODateTimeString } from './common.types'

export type MembershipStatus =
  | 'CREATED'
  | 'ACTIVE'
  | 'FROZEN'
  | 'EXPIRED'
  | 'CANCELLED'

export type MembershipType = 'MONTHLY' | 'YEARLY' | 'VISITS'
export type PaymentStatus = 'PAID' | 'PENDING' | 'FAILED'
export type PaymentCurrency = 'USD' | 'EUR' | 'UAH' | 'TRX' | 'BTC' | 'ETH' | 'USDT'

export interface CreateMembershipRequest {
  clientId: string
  type: MembershipType
  durationMonths?: number
  visitsLimit?: number
}

export interface ExtendMembershipRequest {
  months: number
}

export interface CreatePaymentRequest {
  membershipId: string
  amount: number
  currency: PaymentCurrency
  transactionHash?: string
}

export interface MembershipResponse {
  id: string
  type: MembershipType
  status: MembershipStatus
  startDate: ISODateTimeString
  endDate: ISODateTimeString
  visitsLeft: number | null
}

export interface MembershipShortResponse {
  membershipId: string
  type: MembershipType
  status: MembershipStatus
}

export interface MembershipHistoryResponse {
  memberships: MembershipResponse[]
}

export interface MembershipValidationResponse {
  valid: boolean
  reason: string | null
}

export interface PaymentResponse {
  id: string
  amount: number
  currency: string
  status: PaymentStatus
  paymentDate: ISODateTimeString
  membership: MembershipShortResponse
}

export interface RevenueStatsResponse {
  date: ISODateString
  revenue: number
}
