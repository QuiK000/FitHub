import { useEffect, useState } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useAuthStore } from '../store/useAuthStore'
import type { RoleName } from '../types/user.types'

type ProtectedRouteProps = {
  allowedRoles?: RoleName[]
}

const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
  const { isAuthenticated, token, roles, user, fetchCurrentUser } = useAuthStore()
  const location = useLocation()
  const { t } = useTranslation('common')
  const [loadError, setLoadError] = useState(false)

  useEffect(() => {
    if (isAuthenticated && token && !user && roles.length === 0) {
      const timeout = setTimeout(() => setLoadError(true), 10_000)
      return () => clearTimeout(timeout)
    }
    setLoadError(false)
  }, [isAuthenticated, token, user, roles])

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (isAuthenticated && token && !user && roles.length === 0) {
    if (loadError) {
      return (
        <div className="flex min-h-[50vh] items-center justify-center">
          <div className="flex flex-col items-center gap-3 rounded-2xl border border-border bg-card px-6 py-4 text-sm text-foreground shadow-soft-lg">
            <p>{t('errors.loadFailed')}</p>
            <button
              type="button"
              onClick={() => { setLoadError(false); void fetchCurrentUser() }}
              className="rounded-xl bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90"
            >
              {t('buttons.retry')}
            </button>
          </div>
        </div>
      )
    }
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="flex items-center gap-3 rounded-2xl border border-border bg-card px-4 py-3 text-sm text-foreground shadow-soft-lg">
          <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
          {t('buttons.loading')}
        </div>
      </div>
    )
  }

  if (
    allowedRoles?.length &&
    roles.length > 0 &&
    !allowedRoles.some((role) => roles.includes(role))
  ) {
    return <Navigate to="/dashboard" replace />
  }

  return <Outlet />
}

export default ProtectedRoute
