import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useEffect } from 'react'
import MainLayout from './layouts/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import Register from './pages/Register'
import Profile from './pages/Profile'
import ProtectedRoute from './components/ProtectedRoute'
import { useAuthStore } from './store/useAuthStore'

const App = () => {
  const token = useAuthStore((state) => state.token)
  const fetchCurrentUser = useAuthStore((state) => state.fetchCurrentUser)

  useEffect(() => {
    if (token) {
      void fetchCurrentUser()
    }
  }, [token, fetchCurrentUser])

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route element={<ProtectedRoute />}>
          <Route element={<MainLayout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/profile" element={<Profile />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
