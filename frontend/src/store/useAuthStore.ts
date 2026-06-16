import { create } from 'zustand'
import axios from 'axios'
import { logout as logoutRequest } from '../services/auth.service'
import { getCurrentUser } from '../services/user.service'
import type { RoleName, UserResponse } from '../types/user.types'

type AuthState = {
  token: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  user: UserResponse | null
  roles: RoleName[]
  setAuth: (
    token: string,
    refreshToken?: string | null,
    user?: UserResponse | null,
  ) => void
  setUser: (user: UserResponse | null) => void
  hasRole: (role: RoleName) => boolean
  hasAnyRole: (roles: RoleName[]) => boolean
  clearAuth: () => void
  logout: () => Promise<void>
  fetchCurrentUser: () => Promise<void>
}

const getInitialToken = () => {
  if (typeof window === 'undefined') return null
  try {
    return localStorage.getItem('access_token')
  } catch {
    return null
  }
}

const getInitialRefreshToken = () => {
  if (typeof window === 'undefined') return null
  try {
    return localStorage.getItem('refresh_token')
  } catch {
    return null
  }
}

export const normalizeRoles = (roles: string[] | undefined): RoleName[] =>
  (roles ?? [])
    .map((role) => role.replace(/^ROLE_/, ''))
    .filter((role): role is RoleName =>
      role === 'CLIENT' || role === 'TRAINER' || role === 'ADMIN',
    )

export const useAuthStore = create<AuthState>((set, get) => {
  const initialToken = getInitialToken()
  const initialRefreshToken = getInitialRefreshToken()

  const clearAuthState = () => {
    try {
      if (typeof window !== 'undefined') {
        localStorage.removeItem('access_token')
        localStorage.removeItem('refresh_token')
        localStorage.removeItem('user')
      }
    } catch {
      // ignore persistence errors
    }

    set({
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      user: null,
      roles: [],
    })
  }

  if (typeof window !== 'undefined') {
    window.addEventListener('fithub:auth-cleared', clearAuthState)
    window.addEventListener('fithub:auth-refreshed', () => {
      set({
        token: getInitialToken(),
        refreshToken: getInitialRefreshToken(),
        isAuthenticated: Boolean(getInitialToken()),
      })
    })
  }

  return {
    token: initialToken,
    refreshToken: initialRefreshToken,
    isAuthenticated: Boolean(initialToken),
    user: null,
    roles: [],
    setAuth: (token, refreshToken = null, user = null) => {
      try {
        if (typeof window !== 'undefined') {
          localStorage.setItem('access_token', token)
          if (refreshToken) {
            localStorage.setItem('refresh_token', refreshToken)
          }
        }
      } catch {
        // ignore persistence errors
      }

      set({
        token,
        refreshToken,
        isAuthenticated: true,
        user,
        roles: normalizeRoles(user?.roles),
      })
    },
    setUser: (user) => {
      set({ user, roles: normalizeRoles(user?.roles) })
    },
    hasRole: (role) => get().roles.includes(role),
    hasAnyRole: (roles) => roles.some((role) => get().roles.includes(role)),
    clearAuth: clearAuthState,
    logout: async () => {
      const token = get().token ?? getInitialToken()
      if (token) {
        try {
          await logoutRequest()
        } catch {
          // Local cleanup still wins if the server has already invalidated the token.
        }
      }

      clearAuthState()
    },
    fetchCurrentUser: async () => {
      const token = get().token ?? getInitialToken()
      if (!token) {
        return
      }

      try {
        const user = await getCurrentUser()
        set({
          user,
          roles: normalizeRoles(user.roles),
          isAuthenticated: true,
          token,
          refreshToken: getInitialRefreshToken(),
        })
      } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 401) {
          clearAuthState()
        } else {
          console.error('Failed to fetch current user', error)
          clearAuthState()
        }
      }
    },
  }
})
