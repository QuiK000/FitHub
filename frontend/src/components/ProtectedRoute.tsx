import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import type { RoleName } from '../types/user.types'

type ProtectedRouteProps = {
  allowedRoles?: RoleName[]
}

const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
  const { isAuthenticated, token, roles, user } = useAuthStore()
  const location = useLocation()

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  if (isAuthenticated && token && !user && roles.length === 0) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <div className="flex items-center gap-3 rounded-2xl border border-border bg-card px-4 py-3 text-sm text-foreground shadow-soft-lg">
          <span className="inline-flex h-4 w-4 animate-spin rounded-full border-2 border-border border-t-primary" />
          Loading...
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
