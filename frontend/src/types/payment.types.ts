import type { MembershipType, PaymentCurrency, PaymentStatus } from './membership.types'

export interface CreatePaymentRequest {
  membershipId: string
  amount: number
  currency: PaymentCurrency
  transactionHash?: string
}

export interface PaymentResponse {
  id: string
  amount: number
  currency: PaymentCurrency
  status: PaymentStatus
  paymentDate: string
  membership: {
    membershipId: string
    type: MembershipType
    status: PaymentStatus
  }
}
