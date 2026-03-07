import { create } from 'zustand'
import type { UserResponse } from '../services/user.service'
import { getCurrentUser } from '../services/user.service'

type AuthState = {
  token: string | null
  isAuthenticated: boolean
  user: UserResponse | null
  setAuth: (token: string, user?: UserResponse | null) => void
  logout: () => void
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

export const useAuthStore = create<AuthState>((set, get) => {
  const initialToken = getInitialToken()

  return {
    token: initialToken,
    isAuthenticated: Boolean(initialToken),
    user: null,
    setAuth: (token, user = null) => {
      try {
        if (typeof window !== 'undefined') {
          localStorage.setItem('access_token', token)
        }
      } catch {
        // ignore persistence errors
      }

      set({ token, isAuthenticated: true, user })
    },
    logout: () => {
      try {
        if (typeof window !== 'undefined') {
          localStorage.removeItem('access_token')
        }
      } catch {
        // ignore persistence errors
      }

      set({ token: null, isAuthenticated: false, user: null })
    },
    fetchCurrentUser: async () => {
      const token = get().token ?? getInitialToken()
      if (!token) {
        return
      }

      try {
        const user = await getCurrentUser()
        set({ user, isAuthenticated: true, token })
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error('Failed to fetch current user', error)
      }
    },
  }
})

