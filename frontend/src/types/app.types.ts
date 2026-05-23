import type { UserResponse } from './user.types'

export type ProfileStatus =
  | 'NOT_APPLICABLE'
  | 'MISSING'
  | 'ACTIVE'
  | 'INACTIVE'

export interface AppBootstrapResponse {
  user: UserResponse
  roles: string[]
  profileStatus: ProfileStatus
  onboardingRequired: boolean
  permissions: string[]
}
