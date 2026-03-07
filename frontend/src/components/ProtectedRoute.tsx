import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuthStore } from '../store/useAuthStore'

const ProtectedRoute = () => {
  const { isAuthenticated, token } = useAuthStore()
  const location = useLocation()

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace state={{ from: location }} />
  }

  return <Outlet />
}

export default ProtectedRoute

