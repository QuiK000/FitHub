import axios from 'axios'
import api from './api'
import type {
  MembershipHistoryResponse,
  MembershipResponse,
} from '../types/membership.types'

export type {
  MembershipHistoryResponse,
  MembershipResponse,
  MembershipStatus,
  MembershipType,
} from '../types/membership.types'

export const getMyActiveMembership =
  async (): Promise<MembershipResponse | null> => {
    try {
      const { data } = await api.get<MembershipResponse>('/memberships/me/active')
      return data
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null
      }

      throw error
    }
  }

export const getMyMembershipHistory =
  async (): Promise<MembershipHistoryResponse> => {
    const { data } = await api.get<MembershipHistoryResponse>(
      '/memberships/me/history',
    )
    return data
  }
