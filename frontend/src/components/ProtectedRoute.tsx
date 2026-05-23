import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'
import type { RoleName } from '../types/user.types'

type ProtectedRouteProps = {
  allowedRoles?: RoleName[]
}

const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
  const { isAuthenticated, token, roles } = useAuthStore()
  const location = useLocation()

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace state={{ from: location }} />
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
