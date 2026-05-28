import { useEffect, useState } from 'react'
import axios from 'axios'
import { Navigate, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { getMyClientProfile } from '../services/profile.service'
import { useAuthStore } from '../store/useAuthStore'
import { getApiErrorMessage } from '../utils/errorHandler'
import toast from '../utils/toast'

const ClientOnboardingGate = () => {
  const location = useLocation()
  const navigate = useNavigate()
  const roles = useAuthStore((state) => state.roles)
  const user = useAuthStore((state) => state.user)
  const [isCheckingProfile, setIsCheckingProfile] = useState(false)

  const isClient = roles.includes('CLIENT')
  const isOnboardingRoute = location.pathname === '/onboarding'

  useEffect(() => {
    if (!isClient) {
      return
    }

    if (user?.clientProfile === null && !isOnboardingRoute) {
      navigate('/onboarding', { replace: true })
      return
    }

    let isMounted = true
    setIsCheckingProfile(true)

    void getMyClientProfile()
      .then(() => {
        if (isMounted && isOnboardingRoute) {
          navigate('/dashboard', { replace: true })
        }
      })
      .catch((err) => {
        if (!isMounted) return

        if (
          axios.isAxiosError(err) &&
          (err.response?.status === 404 || err.response?.status === 500)
        ) {
          if (!isOnboardingRoute) {
            navigate('/onboarding', { replace: true })
          }
          return
        }

        toast.error(
          getApiErrorMessage(
            err,
            'Unable to verify your profile status. Please try again.',
          ),
        )
      })
      .finally(() => {
        if (isMounted) {
          setIsCheckingProfile(false)
        }
      })

    return () => {
      isMounted = false
    }
  }, [isClient, isOnboardingRoute, location.pathname, navigate, user?.clientProfile])

  if (!isClient && isOnboardingRoute && roles.length > 0) {
    return <Navigate to="/dashboard" replace />
  }

  if (isClient && isCheckingProfile && !isOnboardingRoute) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="flex items-center gap-3 rounded-2xl border border-border bg-card px-4 py-3 text-sm text-foreground shadow-soft-lg">
          <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
          Checking your profile...
        </div>
      </div>
    )
  }

  return <Outlet />
}

export default ClientOnboardingGate
